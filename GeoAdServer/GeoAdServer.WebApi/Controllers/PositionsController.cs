using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Entities.DTOs;
using GeoAdServer.Domain.Entities.Events;
using GeoAdServer.WebApi.Services;
using System;
using System.Collections.Generic;
using System.Dynamic;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using GeoAdServer.GeneralUtilities;
using GeoAdServer.WebApi.Support;

namespace GeoAdServer.WebApi.Controllers
{
    public class PositionsController : ApiController
    {
        public HttpResponseMessage Post(ChangedPosition chp)
        {
            if (!ModelState.IsValid) return Request.CreateResponseForInvalidModelState();

            EventService.Instance.HandleChangedPosition(chp);

            dynamic data = new ExpandoObject();
            var locations = new LinkedList<LocationDTO>();
            var offers = new LinkedList<dynamic>();

            using (var repos = DataRepos.Instance)
            {
                foreach (var location in repos.Locations.GetAll().Where(x =>
                    double.Parse(x.Lat, CultureInfo.InvariantCulture) < double.Parse(chp.NWCoord.Lat, CultureInfo.InvariantCulture) &&
                    double.Parse(x.Lat, CultureInfo.InvariantCulture) > double.Parse(chp.SECoord.Lat, CultureInfo.InvariantCulture) &&
                    double.Parse(x.Lng, CultureInfo.InvariantCulture) < double.Parse(chp.SECoord.Lng, CultureInfo.InvariantCulture) &&
                    double.Parse(x.Lng, CultureInfo.InvariantCulture) > double.Parse(chp.NWCoord.Lng, CultureInfo.InvariantCulture)))
                {
                    locations.AddLast(location);

                    var nowMillis = (DateTime.Now - new DateTime(1970, 1, 1)).TotalMilliseconds;
                    foreach (var offer in repos.Offers.GetByLocationId(location.Id, (long)nowMillis))
                    {
                        offers.AddLast(offer.AddProperty("LocationName", location.Name));
                    }
                }
            }

            data.Locations = locations.AsQueryable();
            data.Offers = offers.AsQueryable();

            return Request.CreateResponse(HttpStatusCode.OK, (object)data);
        }
    }
}
