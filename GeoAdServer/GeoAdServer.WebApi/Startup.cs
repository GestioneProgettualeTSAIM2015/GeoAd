﻿using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Owin;
using Owin;
using GeoAdServer.WebApi.Services;
using GeoAdServer.EventsHandling;
using GeoAdServer.Domain.Contracts;
using System.Web;
using GeoAdServer.DataAccess;

namespace GeoAdServer.WebApi
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);

            IChangedPositionHandler chpHandler = new PositionsContainer();
            IUserPreferencesHandler userPrefsHandler = new PreferencesContainer(DataRepos.Instance.Preferences);

            EventService.Instance.Add(new AndroidEventPropagator(
                HttpContext.Current.Server.MapPath("~/App_Data/Logs")));

            EventService.Instance.SetPositionsContainer(chpHandler);
            EventService.Instance.SetUserPreferencesHandler(userPrefsHandler);
        }
    }
}
