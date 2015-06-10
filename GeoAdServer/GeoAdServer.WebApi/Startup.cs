using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Owin;
using Owin;
using GeoAdServer.WebApi.Services;
using GeoAdServer.EventsHandling;
using GeoAdServer.Domain.Contracts;

namespace GeoAdServer.WebApi
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);

            IChangedPositionHandler chpHandler = new PositionsContainer();

            EventService.Instance.Add(new AndroidEventPropagator(
                System.Web.HttpContext.Current.Server.MapPath("~/App_Data/Logs")));

            EventService.Instance.SetPositionsContainer(chpHandler);
        }
    }
}
