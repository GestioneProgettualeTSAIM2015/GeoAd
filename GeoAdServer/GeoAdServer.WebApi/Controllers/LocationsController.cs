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
                return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
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

                var result = repos.Locations.DeleteById(id);
                return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
            }
        }
    }
}
