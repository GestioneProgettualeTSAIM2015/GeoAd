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
                return View(offer);
            }
        }
    }
}