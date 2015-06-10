using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Owin;
using Owin;
using GeoAdServer.WebApi.Services;
using GeoAdServer.EventsHandling;
using GeoAdServer.Domain.Contracts;

[assembly: OwinStartup(typeof(GeoAdServer.WebApi.Startup))]

namespace GeoAdServer.WebApi
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);

            IChangedPositionHandler chpHandler = new PositionsContainer();

            EventService.Instance.Add(new AndroidEventPropagator());

            EventService.Instance.Set(chpHandler);
        }
    }
}
