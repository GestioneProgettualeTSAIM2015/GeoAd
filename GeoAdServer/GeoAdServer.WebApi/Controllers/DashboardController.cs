using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Mvc;
using GeoAdServer.WebApi.Support;

namespace GeoAdServer.WebApi.Controllers
{
    public class DashboardController : Controller
    {
        public ActionResult Home()
        {
            var IsAdmin = User.Identity.Name.CompareTo("admin@geoad.com") != 0;
            ViewBag.IsAdmin = IsAdmin;

            if (IsAdmin)

            {
                ViewBag.Name = "Super User";
            }
            else
            {
                ViewBag.Name = User.Identity.Name;
            }
            
            return View();
        }

        public ActionResult NewLocation()
        {
            var IsAdmin = User.Identity.Name.CompareTo("admin@geoad.com") == 0;
            ViewBag.IsAdmin = IsAdmin;
            return View();
        }

        public ActionResult ChangePassword()
        {
            return View();
        }

        public ActionResult ManageLocations()
        {
            var IsAdmin = User.Identity.Name.CompareTo("admin@geoad.com") == 0;
            ViewBag.IsAdmin = IsAdmin;

            var UserId = User.Identity.GetUserId();

            IEnumerable<LocationDTO> vData = DataRepos.Locations./*GetByUserId(UserId)*/GetAll();

            return View(vData);
        }

        public ActionResult EditLocation(int Id)
        {
            var IsAdmin = User.Identity.Name.CompareTo("admin@geoad.com") == 0;
            ViewBag.IsAdmin = IsAdmin;

            LocationDTO vData = DataRepos.Locations.GetById(Id);

            return View(vData);
        }
    }
}
