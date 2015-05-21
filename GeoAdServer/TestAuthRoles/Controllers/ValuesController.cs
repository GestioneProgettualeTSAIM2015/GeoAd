using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace TestAuthRoles.Controllers
{
    public class ValuesController : ApiController
    {
        [Authorize]
        [ActionName("Generic")]
        public IEnumerable<string> GetGeneric()
        {
            return new string[] { "value1", "value2", "value3" };
        }

        [Authorize(Roles="admin")]
        [ActionName("Special")]
        public IEnumerable<string> GetSpecial()
        {
            return new string[] { "wolla", "mitic", "admin", "ij abgfojba", "megarole" };
        }
    }
}
