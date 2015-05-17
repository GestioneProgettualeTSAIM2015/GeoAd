using GeoAdServer.DataAccess;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace GeoAdServer.WebApi.Controllers
{
    public class CategoriesController : ApiController
    {
        public IQueryable<string> Get()
        {
            return DataRepos.Locations.GetCategories().Select(pair => pair.Value).AsQueryable();
        }
    }
}
