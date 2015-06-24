using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Entities;
using GeoAdServer.WebApi.Models;
using GeoAdServer.WebApi.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Data;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using GeoAdServer.Domain.Entities.DTOs;

namespace GeoAdServer.WebApi.Controllers
{
    public class UserSettingsController : ApiController
    {
        [ActionName("Favorites")]
        public HttpResponseMessage PostFav(LocationPreferenceModel prefModel)
        {
            EventService.Instance.SetPreference(prefModel.Id, prefModel.Key, PreferenceTypes.FAVORITE);
            return Request.CreateResponse(HttpStatusCode.NoContent);
        }

        [ActionName("Favorites")]
        public HttpResponseMessage DeleteFav(LocationPreferenceModel prefModel)
        {
            EventService.Instance.DeletePreference(prefModel.Id, prefModel.Key, PreferenceTypes.FAVORITE);
            return Request.CreateResponse(HttpStatusCode.NoContent);
        }

        [ActionName("Ignored")]
        public HttpResponseMessage PostIgn(LocationPreferenceModel prefModel)
        {
            EventService.Instance.SetPreference(prefModel.Id, prefModel.Key, PreferenceTypes.IGNORED);
            return Request.CreateResponse(HttpStatusCode.NoContent);
        }

        [ActionName("Ignored")]
        public HttpResponseMessage DeleteIgn(LocationPreferenceModel prefModel)
        {
            EventService.Instance.DeletePreference(prefModel.Id, prefModel.Key, PreferenceTypes.IGNORED);
            return Request.CreateResponse(HttpStatusCode.NoContent);
        }

        public UserFavsIgn Get(string key)
        {
            var userFavsIgn = new UserFavsIgn();
            var schema = EventService.Instance.Get(key);

            var ids = new List<int>();

            List<int> favs = null;
            if (schema.TryGetValue(PreferenceTypes.FAVORITE, out favs))
            {
                userFavsIgn.Favorites = new List<LocationDTO>(favs.Count);
                ids.AddRange(favs);
            }

            List<int> ign = null;
            if (schema.TryGetValue(PreferenceTypes.IGNORED, out ign))
            {
                userFavsIgn.Ignored = new List<IgnoredLocation>(ign.Count);
                ids.AddRange(ign);
            }

            using (var repos = DataRepos.Instance)
            {
                foreach (var location in repos.Locations.GetByIds(ids))
                {
                    if (favs.Contains(location.Id)) userFavsIgn.Favorites.Add(location);
                    else userFavsIgn.Ignored.Add(new IgnoredLocation
                         {
                             Id = location.Id,
                             Name = location.Name
                         });
                }
            }

            return userFavsIgn;
        }
    }
}
