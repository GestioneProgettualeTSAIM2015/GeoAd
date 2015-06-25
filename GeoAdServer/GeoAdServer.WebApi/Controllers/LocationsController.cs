using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Entities.DTOs;
using GeoAdServer.Domain.Entities.Events;
using GeoAdServer.WebApi.Models;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using GeoAdServer.WebApi.Support;
using GeoAdServer.WebApi.Services;
using System.Configuration;
using System.Globalization;
using GeoAdServer.Domain.Entities;
using System;

namespace GeoAdServer.WebApi.Controllers
{
    public class LocationsController : ApiController
    {
        private readonly static int _MAX_LOCATION_NAME_LENGTH, _MAX_LOCATION_DESCRIPTION_LENGTH;

        static LocationsController()
        {
            _MAX_LOCATION_NAME_LENGTH = int.Parse(ConfigurationManager.AppSettings["maxLocationNameLength"]);
            _MAX_LOCATION_DESCRIPTION_LENGTH = int.Parse(ConfigurationManager.AppSettings["maxLocationDescriptionLength"]);
        }

        public LocationDTO Get(int id)
        {
            using (var repos = DataRepos.Instance)
            {
                return repos.Locations.GetById(id);
            }
        }

        [Authorize]
        public HttpResponseMessage Post(LocationApiModel locationApiModel)
        {
            if (!ModelState.IsValid) return Request.CreateResponseForInvalidModelState();

            using (var repos = DataRepos.Instance)
            {
                Location location = null;
                try
                {
                    location = locationApiModel.CreateLocationFromModel(RequestContext.GetUserId(), repos.Locations, User.Identity);
                }
                catch (ArgumentException aEx)
                {
                    return Request.CreateResponseForUnprocessableEntity(aEx.Message != null ? aEx.Message : null);
                }

                if (location.Name.Length < 1)
                    return Request.CreateResponseForUnprocessableEntity("Location must have a valid name");
                else if (location.Name.Length > _MAX_LOCATION_NAME_LENGTH)
                    location.Name = location.Name.Substring(0, _MAX_LOCATION_NAME_LENGTH);

                if (location.Desc.Length > _MAX_LOCATION_DESCRIPTION_LENGTH)
                    location.Desc = location.Desc.Substring(0, _MAX_LOCATION_DESCRIPTION_LENGTH);

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
        public HttpResponseMessage Put(int id, LocationApiModel locationApiModel)
        {
            if (!ModelState.IsValid) return Request.CreateResponseForInvalidModelState();

            using (var repos = DataRepos.Instance)
            {
                if (!id.IsLocationOwner(RequestContext.GetUserId(), repos.Locations))
                    return Request.CreateResponseForNotOwner();

                Location location = null;
                try
                {
                    location = locationApiModel.CreateLocationFromModel(RequestContext.GetUserId(), repos.Locations, User.Identity);
                }
                catch (ArgumentException aEx)
                {
                    return Request.CreateResponseForUnprocessableEntity(aEx.Message != null ? aEx.Message : null);
                }

                if (location.Name.Length < 1)
                    return Request.CreateResponseForUnprocessableEntity("Location must have a valid name");
                else if (location.Name.Length > _MAX_LOCATION_NAME_LENGTH)
                    location.Name = location.Name.Substring(0, _MAX_LOCATION_NAME_LENGTH);

                if (location.Desc.Length > _MAX_LOCATION_DESCRIPTION_LENGTH)
                    location.Desc = location.Desc.Substring(0, _MAX_LOCATION_DESCRIPTION_LENGTH);

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
                    return Request.CreateResponseForNotOwner();

                foreach (OfferDTO off in repos.Offers.GetByLocationId(id))
                {
                    repos.Offers.DeleteById(off.Id);
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
