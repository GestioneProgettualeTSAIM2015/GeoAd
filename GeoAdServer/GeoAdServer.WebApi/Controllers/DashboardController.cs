using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Mvc;

namespace GeoAdServer.WebApi.Controllers
{
    public class DashboardController : Controller
    {
        public ActionResult Home()
        {
            string vName = User.Identity.Name + "test";
            Boolean vIsSuperuser = User.IsInRole("Superuser");

            ViewBag.userName = vName;
            ViewBag.isSuperuser = !vIsSuperuser;
            
            return View();
        }

        public ActionResult NewPOI()
        {
            if (!User.IsInRole("Superuser"))
            {
                return View();
            }
            
            return View("~/Views/Dashboard/Error.cshtml");
        }

        public ActionResult NewCA()
        {
            return View();
        }

        public ActionResult ChangePassword()
        {
            return View();
        }

        public ActionResult ManagePOI()
        {
            if (!User.IsInRole("Superuser"))
            {
                IEnumerable<LocationDTO> vData = DataRepos.Locations.GetByUserId("2067273727");

                return View(vData);
            }

            return View("~/Views/Dashboard/Error.cshtml");
        }

        public ActionResult ManageCA()
        {
            return View();
        }

    }
}
