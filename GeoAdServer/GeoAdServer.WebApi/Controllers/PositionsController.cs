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

namespace GeoAdServer.WebApi.Controllers
{
    public class PositionsController : ApiController
    {
        public dynamic Post(ChangedPosition chp)
        {
            EventService.Instance.HandleChangedPosition(chp);

            dynamic data = new ExpandoObject();
            var locations = new LinkedList<LocationDTO>();
            var offerings = new LinkedList<dynamic>();

            using (var repos = DataRepos.Instance)
            {
                foreach (var location in repos.Locations.GetAll().Where(x =>
                    double.Parse(x.Lat, CultureInfo.InvariantCulture) < double.Parse(chp.NWCoord.Lat, CultureInfo.InvariantCulture) &&
                    double.Parse(x.Lat, CultureInfo.InvariantCulture) > double.Parse(chp.SECoord.Lat, CultureInfo.InvariantCulture) &&
                    double.Parse(x.Lng, CultureInfo.InvariantCulture) < double.Parse(chp.SECoord.Lng, CultureInfo.InvariantCulture) &&
                    double.Parse(x.Lng, CultureInfo.InvariantCulture) > double.Parse(chp.NWCoord.Lng, CultureInfo.InvariantCulture)))
                {
                    locations.AddLast(location);
                    foreach (var offering in repos.Offerings.GetByLocationId(location.Id))
                    {
                        offerings.AddLast(offering.AddProperty("LocationName", location.Name));
                    }
                }
            }

            data.Locations = locations.AsQueryable();
            data.Offerings = offerings.AsQueryable();

            return data;
        }
    }
}
