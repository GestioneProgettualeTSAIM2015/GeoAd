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
    public class OffersController : ApiController
    {
        private readonly static int _MAX_OFFER_NAME_LENGTH, _MAX_OFFER_DESCRIPTION_LENGTH;

        static OffersController()
        {
            _MAX_OFFER_NAME_LENGTH = int.Parse(ConfigurationManager.AppSettings["maxOfferNameLength"]);
            _MAX_OFFER_DESCRIPTION_LENGTH = int.Parse(ConfigurationManager.AppSettings["maxOfferDescriptionLength"]);
        }

        [Route("api/Offers/FromLocation/{locationId:int}")]
        public IQueryable<OfferDTO> Get(int locationId)
        {
            using (var repos = DataRepos.Instance)
            {
                return repos.Offers.GetByLocationId(locationId).AsQueryable();
            }
        }

        [Authorize]
        public HttpResponseMessage Post(Offer offer)
        {
            using (var repos = DataRepos.Instance)
            {
                if (!ModelState.IsValid) return Request.CreateResponseForInvalidModelState();

                if (!offer.LocationId.IsLocationOwner(RequestContext.GetUserId()))
                    return Request.CreateResponseForNotOwner();

                if (new DateTime(offer.ExpDateMillis) < DateTime.Today)
                    return Request.CreateResponseForUnprocessableEntity();

                if (offer.Name.Length < 1)
                    return Request.CreateResponseForUnprocessableEntity("Offer must have a valid name");
                else if (offer.Name.Length > _MAX_OFFER_NAME_LENGTH)
                    offer.Name = offer.Name.Substring(0, _MAX_OFFER_NAME_LENGTH);

                if (offer.Desc.Length > _MAX_OFFER_DESCRIPTION_LENGTH)
                    offer.Desc = offer.Desc.Substring(0, _MAX_OFFER_DESCRIPTION_LENGTH);

                int id = repos.Offers.Insert(offer);

                //event
                var location = repos.Locations.GetById(offer.LocationId);
                EventService.Instance.Enqueue(new OfferCreated()
                {
                    Offer = offer.BuildDTO(id),
                    LocationId = location.Id,
                    LocationName = location.Name,
                    Lat = location.Lat,
                    Lng = location.Lng
                });

                return id != -1 ? Request.CreateResponse(HttpStatusCode.OK, id) :
                                  Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }

        [Authorize]
        public HttpResponseMessage Put(int id, Offer offer)
        {
            if (!ModelState.IsValid) return Request.CreateResponseForInvalidModelState();

            using (var repos = DataRepos.Instance)
            {
                var dbOffer = repos.Offers.GetById(id);

                var result = false;
                if (dbOffer != null)
                {
                    if (!dbOffer.LocationId.IsLocationOwner(RequestContext.GetUserId(), repos.Locations))
                        return Request.CreateResponseForNotOwner();

                    if (new DateTime(offer.ExpDateMillis) < DateTime.Today)
                        return Request.CreateResponseForUnprocessableEntity();

                    if (offer.Name.Length < 1)
                        return Request.CreateResponseForUnprocessableEntity("offer must have a valid name");
                    else if (offer.Name.Length > _MAX_OFFER_NAME_LENGTH)
                        offer.Name = offer.Name.Substring(0, _MAX_OFFER_NAME_LENGTH);

                    if (offer.Desc.Length > _MAX_OFFER_DESCRIPTION_LENGTH)
                        offer.Desc = offer.Desc.Substring(0, _MAX_OFFER_DESCRIPTION_LENGTH);

                    result = repos.Offers.Update(id, offer);
                }

                if (result)
                {
                    //event
                    var location = repos.Locations.GetById(offer.LocationId);
                    EventService.Instance.Enqueue(new OfferUpdated()
                    {
                        Offer = offer.BuildDTO(id),
                        LocationId = location.Id,
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
                var dbOffer = repos.Offers.GetById(id);

                var result = false;
                if (dbOffer != null)
                {
                    if (!dbOffer.LocationId.IsLocationOwner(RequestContext.GetUserId(), repos.Locations))
                        return Request.CreateResponseForNotOwner();

                    result = repos.Offers.DeleteById(id);
                }

                if (result)
                {
                    //event
                    var location = repos.Locations.GetById(dbOffer.LocationId);
                    EventService.Instance.Enqueue(new OfferDeleted()
                    {
                        OfferId = id,
                        LocationId = location.Id,
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
