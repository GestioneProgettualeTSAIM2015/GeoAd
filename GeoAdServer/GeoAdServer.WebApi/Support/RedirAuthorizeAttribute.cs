using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;
using System.Web.Routing;

namespace GeoAdServer.WebApi.Support
{
    public class RedirAuthorizeAttribute : AuthorizeAttribute
    {
        public string RedirectToController { get; set; }

        public string RedirectToAction { get; set; }

        protected override void HandleUnauthorizedRequest(AuthorizationContext filterContext)
        {
            if (RedirectToController == null) base.HandleUnauthorizedRequest(filterContext);

            filterContext.Result = new RedirectToRouteResult(new RouteValueDictionary(
            new
            {
                controller = RedirectToController,
                action = RedirectToAction ?? "Index"
            }));
        }
    }
}
