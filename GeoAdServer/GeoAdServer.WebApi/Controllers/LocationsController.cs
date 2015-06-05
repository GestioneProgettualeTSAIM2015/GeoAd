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

namespace GeoAdServer.WebApi.Controllers
{
    public class LocationsController : ApiController
    {
        public LocationDTO Get(int id)
        {
            using (ILocationsRepository repo = DataRepos.Locations)
            {
                return repo.GetById(id);
            }
        }

        public IQueryable<LocationDTO> GetWithKey(string key)
        {
            using (ILocationsRepository repo = DataRepos.Locations)
            {
                return repo.GetAll().AsQueryable();
            }
        }

        public IQueryable<LocationDTO> Get(string userId)
        {
            using (ILocationsRepository repo = DataRepos.Locations)
            {
                return repo.GetByUserId(userId).AsQueryable();
            }
        }

        [Authorize]
        public HttpResponseMessage Post([FromBody]LocationApiModel locationApiModel)
        {
            using (ILocationsRepository repo = DataRepos.Locations)
            {
                if (locationApiModel == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

                var location = locationApiModel.CreateLocationFromModel(RequestContext.GetUserId(), repo, User.Identity);
                int id = repo.Insert(location);
                return id != -1 ? Request.CreateResponse(HttpStatusCode.OK, id) :
                                  Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }

        [Authorize]
        public HttpResponseMessage Put(int id, [FromBody]LocationApiModel locationApiModel)
        {
            using (ILocationsRepository repo = DataRepos.Locations)
            {
                if (locationApiModel == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

                if (!id.IsLocationOwner(RequestContext.GetUserId()))
                    return Request.CreateResponse(HttpStatusCode.Unauthorized);

                var location = locationApiModel.CreateLocationFromModel(RequestContext.GetUserId(), repo, User.Identity);
                var result = repo.Update(id, location);
                return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
            }
        }

        [Authorize]
        public HttpResponseMessage Delete(int id)
        {
            using (ILocationsRepository repoLoc = DataRepos.Locations)
            {
                if (!id.IsLocationOwner(RequestContext.GetUserId()))
                    return Request.CreateResponse(HttpStatusCode.Unauthorized);

                using (IOfferingsRepository repoOff = DataRepos.Offerings)
                {
                    foreach (OfferingDTO off in DataRepos.Offerings.GetByLocationId(id))
                    {
                        DataRepos.Offerings.DeleteById(off.Id);
                    }

                    using (IPhotosRepository repoPho = DataRepos.Photos)
                    {
                        foreach (PhotoDTO pho in repoPho.GetByLocationId(id)) repoPho.Delete(pho.Id);

                        var result = repoLoc.DeleteById(id);
                        return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
                    }
                }
            }
        }
    }
}
