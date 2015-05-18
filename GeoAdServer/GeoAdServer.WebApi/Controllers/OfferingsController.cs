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

namespace GeoAdServer.WebApi.Controllers
{
    public class OfferingsController : ApiController
    {
        public IQueryable<OfferingDTO> Get()
        {
            return DataRepos.Offerings.GetAll().AsQueryable();
        }
        
        public IQueryable<OfferingDTO> Get(int id)
        {
            return DataRepos.Offerings.GetByLocationId(id).AsQueryable();
        }

        [Authorize]
        public HttpResponseMessage Post(Offering offering)
        {
            if (offering == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

            if (!offering.LocationId.IsLocationOwner(RequestContext.GetUserId()))
                return Request.CreateResponse(HttpStatusCode.Unauthorized);

            int id = DataRepos.Offerings.Insert(offering);
            return id != -1 ? Request.CreateResponse(HttpStatusCode.OK, id) :
                              Request.CreateResponse(HttpStatusCode.InternalServerError);
        }

        [Authorize]
        public HttpResponseMessage Put(int id, [FromBody]Offering offering)
        {
            if (offering == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

            if (!id.IsLocationOwner(RequestContext.GetUserId()))
                return Request.CreateResponse(HttpStatusCode.Unauthorized);

            var result = DataRepos.Offerings.Update(id, offering);
            return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
        }

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
