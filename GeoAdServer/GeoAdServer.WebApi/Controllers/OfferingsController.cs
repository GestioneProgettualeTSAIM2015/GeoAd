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

namespace GeoAdServer.WebApi.Controllers
{
    public class OfferingsController : ApiController
    {
        public IQueryable<OfferingDTO> Get()
        {
            using (IOfferingsRepository repo = DataRepos.Offerings)
            {
                return repo.GetAll().AsQueryable();
            }
        }
        
        public IQueryable<OfferingDTO> Get(int id)
        {
            using (IOfferingsRepository repo = DataRepos.Offerings)
            {
                return repo.GetByLocationId(id).AsQueryable();
            }
        }

        [Authorize]
        public HttpResponseMessage Post(Offering offering)
        {
            using (IOfferingsRepository repo = DataRepos.Offerings)
            {
                if (offering == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

                if (!offering.LocationId.IsLocationOwner(RequestContext.GetUserId()))
                    return Request.CreateResponse(HttpStatusCode.Unauthorized);

                int id = repo.Insert(offering);
                return id != -1 ? Request.CreateResponse(HttpStatusCode.OK, id) :
                                  Request.CreateResponse(HttpStatusCode.InternalServerError);
            }
        }

        [Authorize]
        public HttpResponseMessage Put(int id, [FromBody]Offering offering)
        {
            if (offering == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

            using (IOfferingsRepository repo = DataRepos.Offerings)
            {
                var dbOffering = repo.GetAll().Where(x => x.Id == id).SingleOrDefault();

                var result = false;
                if (dbOffering != null)
                {
                    if (!dbOffering.LocationId.IsLocationOwner(RequestContext.GetUserId()))
                        return Request.CreateResponse(HttpStatusCode.Unauthorized);

                    result = repo.Update(id, offering);
                }

                return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
            }     
        }

        [Authorize]
        public HttpResponseMessage Delete(int id)
        {
            using (IOfferingsRepository repo = DataRepos.Offerings)
            {
                var dbOffering = repo.GetAll().Where(x => x.Id == id).SingleOrDefault();

                var result = false;
                if (dbOffering != null)
                {
                    if (!dbOffering.LocationId.IsLocationOwner(RequestContext.GetUserId()))
                        return Request.CreateResponse(HttpStatusCode.Unauthorized);

                    result = repo.DeleteById(id);
                }

                return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
            }
        }
    }
}
