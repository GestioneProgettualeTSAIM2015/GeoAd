using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Castle.Windsor;
using GeoAdServer.Domain.Contracts;
using Castle.MicroKernel.Registration;
using GeoAdServer.Postgresql;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using System.Linq;

namespace GeoAdServer.Domain.ContractsImplementations.Test
{
    [TestClass]
    public class OffersRepository
    {
        [TestMethod]
        public void OffersRepositoryTest()
        {
            //Container initialization
            string connectionString = "";

            var container = new WindsorContainer();
            container.Register(
                Component.For<ILocationsRepository>()
                         .ImplementedBy<PostgresqLocationsRepository>()
                         .DependsOn(Dependency.OnValue("connectionString", connectionString))
                         .Named("pgrslocrepo"));
            container.Register(
                Component.For<IOffersRepository>()
                         .ImplementedBy<PostgresqOffersRepository>()
                         .DependsOn(Dependency.OnValue("connectionString", connectionString))
                         .Named("pgrsoffrepo"));

            //Test
            var locationsRepository = container.Resolve<ILocationsRepository>();
            var offersRepository = container.Resolve<IOffersRepository>();

            var locationEnum = locationsRepository.GetAll().GetEnumerator();
            locationEnum.MoveNext();
            int locationId = locationEnum.Current.Id;

            var offer = new Offer
            {
                LocationId = locationId,
                Desc = "Test offers",
                ExpDateMillis = (long) (DateTime.Now.AddHours(24) - new DateTime(1970, 1, 1)).TotalMilliseconds
            };
            int id = offersRepository.Insert(offer);
            var offerDTO = offersRepository.GetByLocationId(locationId).Where(off => off.Id == id).Single();
            var copy = offersRepository.GetAll().Where(off => off.Equals(offerDTO)).Single();
            offer.Desc = "new desc";
            Assert.IsTrue(offersRepository.Update(id, offer));
            Assert.IsFalse(offersRepository.Update(-1, offer));
            Assert.IsNull(offersRepository.GetAll().Where(off => off.Equals(offerDTO)).SingleOrDefault());
            Assert.IsTrue(offersRepository.DeleteById(id));
            Assert.IsNull(offersRepository.GetByLocationId(locationId).Where(off => off.Id == id).SingleOrDefault());
        }
    }
}
