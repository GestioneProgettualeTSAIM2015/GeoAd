using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Owin;
using Owin;

[assembly: OwinStartup(typeof(GeoAdServer.WebApi.Startup))]

namespace GeoAdServer.WebApi
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
            DataAccess.DataRepos.Locations.ToString(); //forces initialization
        }
    }
}
