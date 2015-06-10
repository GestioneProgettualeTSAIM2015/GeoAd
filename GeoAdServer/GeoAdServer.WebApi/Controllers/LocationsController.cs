using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using GeoAdServer.Postgresql;
using GeoAdServer.WebApi.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using GeoAdServer.WebApi.Support;
using GeoAdServer.Domain.Entities.Events;
using GeoAdServer.WebApi.Services;

namespace GeoAdServer.WebApi.Controllers
{
    public class LocationsController : ApiController
    {
        public LocationDTO Get(int id)
        {
            using (var repos = DataRepos.Instance)
            {
                return repos.Locations.GetById(id);
            }
        }

        public IQueryable<LocationDTO> GetWithKey([FromUri]ChangedPosition chp)
        {
            using (var repos = DataRepos.Instance)
            {
                return repos.Locations.GetAll().Where(x =>
                    Double.Parse(x.Lat) < Double.Parse(chp.NWCoord.Lat) &&
                    Double.Parse(x.Lat) > Double.Parse(chp.SECoord.Lat) &&
                    Double.Parse(x.Lng) < Double.Parse(chp.SECoord.Lng) &&
                    Double.Parse(x.Lng) > Double.Parse(chp.NWCoord.Lng)).AsQueryable();
            }
        }

        public IQueryable<LocationDTO> Get(string userId)
        {
            using (var repos = DataRepos.Instance)
            {
                return repos.Locations.GetByUserId(userId).AsQueryable();
            }
        }

        [Authorize]
        public HttpResponseMessage Post([FromBody]LocationApiModel locationApiModel)
        {
            using (var repos = DataRepos.Instance)
            {
                if (locationApiModel == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

                var location = locationApiModel.CreateLocationFromModel(RequestContext.GetUserId(), repos.Locations, User.Identity);
                int id = repos.Locations.Insert(location);

                //event
                EventService.Instance.Enqueue(new LocationCreated()
                {
                    Location = locationApiModel.BuildDTO(id, location.Type),
                });

                return id != -1 ? Request.CreateResponse(HttpStatusCode.OK, id) :
                                  Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }

        [Authorize]
        public HttpResponseMessage Put(int id, [FromBody]LocationApiModel locationApiModel)
        {
            using (var repos = DataRepos.Instance)
            {
                if (locationApiModel == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

                if (!id.IsLocationOwner(RequestContext.GetUserId(), repos.Locations))
                    return Request.CreateResponse(HttpStatusCode.Unauthorized);

                var location = locationApiModel.CreateLocationFromModel(RequestContext.GetUserId(), repos.Locations, User.Identity);
                var result = repos.Locations.Update(id, location);

                if (result)
                {
                    //event
                    EventService.Instance.Enqueue(new LocationUpdated()
                    {
                        Location = locationApiModel.BuildDTO(id, location.Type),
                    });

                    return Request.CreateResponse(HttpStatusCode.NoContent);
                }

                return Request.CreateResponse(HttpStatusCode.NotFound);
            }
        }

        [Authorize]
        public HttpResponseMessage Delete(int id)
        {
            using (var repos = DataRepos.Instance)
            {
                if (!id.IsLocationOwner(RequestContext.GetUserId(), repos.Locations))
                    return Request.CreateResponse(HttpStatusCode.Unauthorized);

                foreach (OfferingDTO off in repos.Offerings.GetByLocationId(id))
                {
                    repos.Offerings.DeleteById(off.Id);
                }

                foreach (PhotoDTO pho in repos.Photos.GetByLocationId(id)) repos.Photos.Delete(pho.Id);

                var location = repos.Locations.GetById(id);
                var result = repos.Locations.DeleteById(id);

                if (result)
                {
                    //event
                    EventService.Instance.Enqueue(new LocationDeleted()
                    {
                        LocationId = id,
                        Lat = location.Lat,
                        Lng = location.Lng
                    });

                    return Request.CreateResponse(HttpStatusCode.NoContent);
                }

                return Request.CreateResponse(HttpStatusCode.NotFound);
            }
        }
    }
}
