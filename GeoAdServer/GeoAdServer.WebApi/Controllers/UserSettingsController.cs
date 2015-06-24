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

        public Dictionary<PreferenceTypes, List<int>> Get(string key)
        {
            return EventService.Instance.Get(key);
        }
    }
}
