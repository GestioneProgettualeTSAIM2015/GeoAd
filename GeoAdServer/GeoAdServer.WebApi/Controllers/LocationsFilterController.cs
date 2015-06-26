using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Entities.DTOs;
using GeoAdServer.WebApi.Models;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using GeoAdServer.WebApi.Support;
using System.Net.Http.Formatting;
using System.Web.Http.OData;

namespace GeoAdServer.WebApi.Controllers
{
    [RoutePrefix("api/LocationsFilter")]
    public class LocationsFilterController : ApiController
    {
        private readonly static double _MAX_LOCATIONS_SEARCH_R, _MIN_LOCATIONS_SEARCH_R;

        static LocationsFilterController()
        {
            _MAX_LOCATIONS_SEARCH_R = double.Parse(ConfigurationManager.AppSettings["maxLocationsSearchR"], CultureInfo.InvariantCulture);
            _MIN_LOCATIONS_SEARCH_R = double.Parse(ConfigurationManager.AppSettings["minLocationsSearchR"], CultureInfo.InvariantCulture);
        }

        [HttpGet]
        [EnableQuery]
        [Route("Around")]
        public HttpResponseMessage Get([FromUri]SearchPosition pos)
        {
            if (!ModelState.IsValid) return Request.CreateResponseForInvalidModelState();

            if (pos.R > _MAX_LOCATIONS_SEARCH_R) pos.R = _MAX_LOCATIONS_SEARCH_R;
            else if (pos.R <= _MIN_LOCATIONS_SEARCH_R) pos.R = _MIN_LOCATIONS_SEARCH_R;
            else pos.R = (int)pos.R;

            using (var repos = DataRepos.Instance)
            {
                return Request.CreateResponse(HttpStatusCode.OK,
                    repos.Locations.GetAllAround(pos.P.Lat, pos.P.Lng, pos.R));
            }
        }

        [HttpGet]
        [Authorize]
        [EnableQuery]
        [Route("MyLocations")]
        public HttpResponseMessage Get()
        {
            using (var repos = DataRepos.Instance)
            {
                return Request.CreateResponse(HttpStatusCode.OK, repos.Locations.GetByUserId(RequestContext.GetUserId()));
            }
        }
    }
}
