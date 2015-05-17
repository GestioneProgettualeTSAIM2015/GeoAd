using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

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

        public HttpResponseMessage Post(Offering offering)
        {
            int id = DataRepos.Offerings.Insert(offering);
            return id != -1 ? Request.CreateResponse(HttpStatusCode.OK, 1) :
                              Request.CreateResponse(HttpStatusCode.InternalServerError);
        }

        public HttpResponseMessage Put(int id, [FromBody]Offering offering)
        {
            var result = DataRepos.Offerings.Update(id, offering);
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
}
