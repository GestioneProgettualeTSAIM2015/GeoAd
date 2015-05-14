using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using GeoAdServer.Domain.Contracts;
using System.Collections.Generic;
using Castle.Windsor;
using Castle.MicroKernel.Registration;
using GeoAdServer.Postgresql;

namespace GeoAdServer.Domain.ContractsImplementations.Test
{
    [TestClass]
    public class LocationsRepositoryTest
    {
        [TestMethod]
        public void TestMethod1()
        {
            //Container initialization
            string connectionString = "Server=localhost;Port=5012;UserId=postgres;Password=admin;Database=GeoAdDb";

            var container = new WindsorContainer();
            container.Register(
                Component.For<ILocationsRepository>()
                         .ImplementedBy<PostgresqlLocationsRepository>()
                         .DependsOn(Dependency.OnValue("connectionString", connectionString))
                         .Named("pgrslocrepo"));

            //Category Test
            string newCategory = "aagghhtest";
            IList<string> types = new List<string>(new string[]
            {
                "ca", //default
                "poi", //default
                newCategory
            });

            ILocationsRepository locationRepository = container.Resolve<ILocationsRepository>();

            locationRepository.DeleteCategory(newCategory);
            Assert.IsTrue(locationRepository.InsertCategory(newCategory));

            foreach (KeyValuePair<int, string> pair in locationRepository.GetCategories())
                Assert.IsTrue(types.Remove(pair.Value.ToLower()));

            Assert.IsTrue(types.Count == 0);

            Assert.IsTrue(locationRepository.DeleteCategory(newCategory));

            types.Add("ca");
            types.Add("poi");
            foreach (KeyValuePair<int, string> pair in locationRepository.GetCategories())
                Assert.IsTrue(types.Remove(pair.Value.ToLower()));

            Assert.IsTrue(types.Count == 0);
        }
    }
}
