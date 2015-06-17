using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using GeoAdServer.WebApi.Support;
using GeoAdServer.Domain.Contracts;
using GeoAdServer.WebApi.Services;
using GeoAdServer.Domain.Entities.Events;
using System.Configuration;

namespace GeoAdServer.WebApi.Controllers
{
    public class OfferingsController : ApiController
    {
        private readonly static int MAX_OFFERING_DESCRIPTION_LENGTH;

        static OfferingsController()
        {
            MAX_OFFERING_DESCRIPTION_LENGTH = int.Parse(ConfigurationManager.AppSettings["maxOfferingDescriptionLength"]);
        }

        public IQueryable<OfferingDTO> Get()
        {
            using (var repos = DataRepos.Instance)
            {
                return repos.Offerings.GetAll().AsQueryable();
            }
        }
        
        public IQueryable<OfferingDTO> Get(int id)
        {
            using (var repos = DataRepos.Instance)
            {
                return repos.Offerings.GetByLocationId(id).AsQueryable();
            }
        }

        [Authorize]
        public HttpResponseMessage Post(Offering offering)
        {
            using (var repos = DataRepos.Instance)
            {
                if (offering == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

                if (!offering.LocationId.IsLocationOwner(RequestContext.GetUserId()))
                    return Request.CreateResponse(HttpStatusCode.Unauthorized);

                if (offering.ExpDateMillis < (DateTime.Today).Millisecond)
                    return Request.CreateResponse((HttpStatusCode)422, "Unprocessable Entity");

                offering.Desc = offering.Desc.Substring(0, MAX_OFFERING_DESCRIPTION_LENGTH);
                int id = repos.Offerings.Insert(offering);

                //event
                var location = repos.Locations.GetById(offering.LocationId);
                EventService.Instance.Enqueue(new OfferingCreated()
                {
                    Offering = offering.BuildDTO(id),
                    LocationName = location.Name,
                    Lat = location.Lat,
                    Lng = location.Lng
                });

                return id != -1 ? Request.CreateResponse(HttpStatusCode.OK, id) :
                                  Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }

        [Authorize]
        public HttpResponseMessage Put(int id, [FromBody]Offering offering)
        {
            if (offering == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

            if (offering.ExpDateMillis < (DateTime.Today).Millisecond)
                return Request.CreateResponse((HttpStatusCode)422, "Unprocessable Entity");

            using (var repos = DataRepos.Instance)
            {
                var dbOffering = repos.Offerings.GetById(id);

                var result = false;
                if (dbOffering != null)
                {
                    if (!dbOffering.LocationId.IsLocationOwner(RequestContext.GetUserId(), repos.Locations))
                        return Request.CreateResponse(HttpStatusCode.Unauthorized);

                    offering.Desc = offering.Desc.Substring(0, MAX_OFFERING_DESCRIPTION_LENGTH);
                    result = repos.Offerings.Update(id, offering);
                }

                if (result)
                {
                    //event
                    var location = repos.Locations.GetById(offering.LocationId);
                    EventService.Instance.Enqueue(new OfferingUpdated()
                    {
                        Offering = offering.BuildDTO(id),
                        LocationName = location.Name,
                        Lat = location.Lat,
                        Lng = location.Lng
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
                var dbOffering = repos.Offerings.GetById(id);

                var result = false;
                if (dbOffering != null)
                {
                    if (!dbOffering.LocationId.IsLocationOwner(RequestContext.GetUserId(), repos.Locations))
                        return Request.CreateResponse(HttpStatusCode.Unauthorized);

                    result = repos.Offerings.DeleteById(id);
                }

                if (result)
                {
                    //event
                    var location = repos.Locations.GetById(dbOffering.LocationId);
                    EventService.Instance.Enqueue(new OfferingDeleted()
                    {
                        OfferingId = id,
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
