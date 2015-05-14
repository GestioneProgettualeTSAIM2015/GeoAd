using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.OData;

namespace GeoAdServer.WebApi.Controllers
{
    [Authorize]
    public class ValuesController : ODataController
    {
        // GET odata/values
        [Authorize]
        [EnableQuery]
        public IQueryable<string> Get()
        {
            return (new string[] { "value1", "value2", RequestContext.Principal.Identity.GetHashCode().ToString()}).AsQueryable();
        }

        // GET api/values/5
        public string Get(int id)
        {
            return "value";
        }

        // POST api/values
        public void Post([FromBody]string value)
        {
        }

        // PUT api/values/5
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/values/5
        public void Delete(int id)
        {
        }
    }
}
