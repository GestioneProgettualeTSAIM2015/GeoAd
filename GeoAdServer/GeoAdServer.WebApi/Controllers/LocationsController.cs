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
        [ActionName("All")]
        public IQueryable<LocationDTO> GetAll()
        {
            return DataRepos.Locations.GetAll().AsQueryable();
        }

        [ActionName("FromUser")]
        public IQueryable<LocationDTO> GetFromUser(string id)
        {
            return DataRepos.Locations.GetByUserId(id).AsQueryable();
        }

        [ActionName("ById")]
        public LocationDTO GetById(int id)
        {
            return DataRepos.Locations.GetById(id);
        }

        [HttpPost]
        public HttpResponseMessage Insert([FromBody]LocationApiModel locationApiModel)
        {
            var location = locationApiModel.CreateLocationFromModel(
                    RequestContext.Principal.Identity.Name.GetHashCode().ToString(),
                    DataRepos.Locations);
            int id = DataRepos.Locations.Insert(location);
            return id != -1 ? Request.CreateResponse(HttpStatusCode.OK, 1) :
                              Request.CreateResponse(HttpStatusCode.InternalServerError);
        }

        [HttpPut]
        [ActionName("ById")]
        public HttpResponseMessage Update(int id, [FromBody]LocationApiModel locationApiModel)
        {
            var location = locationApiModel.CreateLocationFromModel(
                    RequestContext.Principal.Identity.Name.GetHashCode().ToString(),
                    DataRepos.Locations);
            var result = DataRepos.Locations.Update(id, location);
            return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
        }

        [HttpDelete]
        [ActionName("ById")]
        public HttpResponseMessage DeleteById(int id)
        {
            foreach (OfferingDTO off in DataRepos.Offerings.GetByLocationId(id))
            {
                DataRepos.Offerings.DeleteById(off.Id);
            }

            foreach (PhotoDTO pho in DataRepos.Photos.GetByLocationId(id)) DataRepos.Offerings.DeleteById(pho.Id);

            var result = DataRepos.Locations.DeleteById(id);
            return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
        }

        [HttpGet]
        [ActionName("Categories")]
        public IQueryable<string> GetCategories()
        {
            return DataRepos.Locations.GetCategories().Select(pair => pair.Value).AsQueryable();
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
            var type = true ? "ca" : "poi";

            return new Location
            {
                UserId = userId,
                PCatId = pCatId,
                SCatId = sCatId,
                Name = model.Name,
                Lat = model.Lat,
                Lng = model.Lng,
                Desc = model.Desc,
                Type = type
            };
        }
    }
}
