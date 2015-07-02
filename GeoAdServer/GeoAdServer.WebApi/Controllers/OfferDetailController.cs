using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace GeoAdServer.WebApi.Controllers
{
    public class OfferDetailController : Controller
    {
        public ActionResult Show(int id)
        {
            using (var repos = DataRepos.Instance)
            {
                OfferDTO offer = repos.Offers.GetById(id);
                if (offer == null) return NewErrorView();
                ViewBag.LocationName = repos.Locations.GetById(offer.LocationId).Name;
                return View(offer);
            }
        }

        private ViewResult NewErrorView()
        {
            ViewBag.Title = "404 Not Found";
            ViewBag.Message = "It seems like we couldn't find the specified offer...";
            return View("~/Views/Dashboard/Error.cshtml");
        }
    }
}