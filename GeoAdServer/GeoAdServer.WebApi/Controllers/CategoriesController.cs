using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities.DTOs;
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
        public Dictionary<string, IList<string>> Get()
        {
            using (var repos =  DataRepos.Instance)
            {
                return repos.Locations.GetCategories();
            }
        }
    }
}
