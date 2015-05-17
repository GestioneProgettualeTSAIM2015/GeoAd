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

namespace GeoAdServer.WebApi.Controllers
{
    [Authorize]
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

        public HttpResponseMessage Post([FromBody]LocationApiModel locationApiModel)
        {
            var location = locationApiModel.CreateLocationFromModel(
                    RequestContext.Principal.Identity.Name.GetHashCode().ToString(),
                    DataRepos.Locations);
            int id = DataRepos.Locations.Insert(location);
            return id != -1 ? Request.CreateResponse(HttpStatusCode.OK, id) :
                              Request.CreateResponse(HttpStatusCode.InternalServerError);
        }

        public HttpResponseMessage Put(int id, [FromBody]LocationApiModel locationApiModel)
        {
            var location = locationApiModel.CreateLocationFromModel(
                    RequestContext.Principal.Identity.Name.GetHashCode().ToString(),
                    DataRepos.Locations);
            var result = DataRepos.Locations.Update(id, location);
            return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
        }

        public HttpResponseMessage Delete(int id)
        {
            foreach (OfferingDTO off in DataRepos.Offerings.GetByLocationId(id))
            {
                DataRepos.Offerings.DeleteById(off.Id);
            }

            foreach (PhotoDTO pho in DataRepos.Photos.GetByLocationId(id)) DataRepos.Offerings.DeleteById(pho.Id);

            var result = DataRepos.Locations.DeleteById(id);
            return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
        }
    }

    public static class LocationWrapper
    {
        public static Location CreateLocationFromModel(this LocationApiModel model, string userId, ILocationsRepository repository)
        {
            var categories = repository.GetCategories();

            if (!categories.ContainsValue(model.PCat)) repository.InsertCategory(model.PCat);
            if (model.SCat != null && !categories.ContainsValue(model.SCat)) repository.InsertCategory(model.SCat);

            int pCatId = categories.FirstOrDefault(pair => pair.Value == model.PCat).Key;
            int? sCatId = categories.FirstOrDefault(pair => pair.Value == model.SCat).Key;
            if (sCatId == 0) sCatId = null;

            //check auth
            var typeId = true ? 0 : 1;

            return new Location
            {
                UserId = userId,
                PCatId = pCatId,
                SCatId = sCatId,
                Name = model.Name,
                Lat = model.Lat,
                Lng = model.Lng,
                Desc = model.Desc,
                Type = Types.Values[typeId]
            };
        }
    }
}
