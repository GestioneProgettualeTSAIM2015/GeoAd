using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Mvc;

namespace GeoAdServer.WebApi.Controllers
{
    public class LoginController : Controller
    {
        public ActionResult Login()
        {
            ViewBag.Title = "Login Page";

            return View();
        }
    }
}
