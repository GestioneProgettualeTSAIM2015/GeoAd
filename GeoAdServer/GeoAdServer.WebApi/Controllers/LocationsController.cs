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
        public IQueryable<LocationDTO> Get()
        {
            return DataRepos.Locations.GetAll().AsQueryable();
        }

        public LocationDTO Get(int id)
        {
            return DataRepos.Locations.GetById(id);
        }

        public IQueryable<LocationDTO> Get(string userId)
        {
            return DataRepos.Locations.GetByUserId(userId).AsQueryable();
        }

        [Authorize]
        public HttpResponseMessage Post([FromBody]LocationApiModel locationApiModel)
        {
            if (locationApiModel == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

            var location = locationApiModel.CreateLocationFromModel(RequestContext.GetUserId(), DataRepos.Locations);
            int id = DataRepos.Locations.Insert(location);
            return id != -1 ? Request.CreateResponse(HttpStatusCode.OK, id) :
                              Request.CreateResponse(HttpStatusCode.InternalServerError);
        }

        [Authorize]
        public HttpResponseMessage Put(int id, [FromBody]LocationApiModel locationApiModel)
        {
            if (locationApiModel == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

            if (!id.IsLocationOwner(RequestContext.GetUserId()))
                return Request.CreateResponse(HttpStatusCode.Unauthorized);

            var location = locationApiModel.CreateLocationFromModel(RequestContext.GetUserId(), DataRepos.Locations);
            var result = DataRepos.Locations.Update(id, location);
            return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
        }

        [Authorize]
        public HttpResponseMessage Delete(int id)
        {
            if (!id.IsLocationOwner(RequestContext.GetUserId()))
                return Request.CreateResponse(HttpStatusCode.Unauthorized);

            foreach (OfferingDTO off in DataRepos.Offerings.GetByLocationId(id))
            {
                DataRepos.Offerings.DeleteById(off.Id);
            }

            foreach (PhotoDTO pho in DataRepos.Photos.GetByLocationId(id)) DataRepos.Offerings.DeleteById(pho.Id);

            var result = DataRepos.Locations.DeleteById(id);
            return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
        }
    }
}
