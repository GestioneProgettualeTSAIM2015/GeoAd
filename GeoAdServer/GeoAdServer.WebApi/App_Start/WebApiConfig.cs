﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Web.Http;
using Microsoft.Owin.Security.OAuth;
using Newtonsoft.Json.Serialization;
using System.Web.Http.OData.Builder;
using System.Web.Http.OData.Extensions;
using GeoAdServer.Domain.Entities.DTOs;

namespace GeoAdServer.WebApi
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            // Web API configuration and services
            // Configure Web API to use only bearer token authentication.
            config.SuppressDefaultHostAuthentication();
            config.Filters.Add(new HostAuthenticationFilter(OAuthDefaults.AuthenticationType));

            // Web API routes
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute(
                name: "FromUserApi",
                routeTemplate: "api/{controller}/fromuser/{userId}"
            );

            config.Routes.MapHttpRoute(
                name: "FromLocationApi",
                routeTemplate: "api/{controller}/fromlocation/{locationId}"
            );

            config.Routes.MapHttpRoute(
                name: "DefaultRoute",
                routeTemplate: "api/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );

            /*ODataConventionModelBuilder builder = new ODataConventionModelBuilder();
            builder.EntitySet<LocationDTO>("Location");
            config.Routes.MapODataServiceRoute("odataRoute", "odata", builder.GetEdmModel());*/

            config.AddODataQueryFilter();
        }
    }
}