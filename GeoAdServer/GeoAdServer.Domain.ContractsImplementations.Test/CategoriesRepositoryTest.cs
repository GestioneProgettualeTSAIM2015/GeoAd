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
    public class CategoriesRepositoryTest
    {
        [TestMethod]
        public void TestMethod1()
        {
            //Container initialization
            string connectionString = "Server=localhost;Port=5012;UserId=postgres;Password=admin;Database=GeoAdDb";
            var container = new WindsorContainer();
            container.Register(
                Component.For<ICategoriesRepository>()
                         .ImplementedBy<PostgresqlCategoriesRepository>()
                         .DependsOn(Dependency.OnValue("connectionString", connectionString))
                         .Named("pgrscatrepo"));

            //Test
            IList<string> types = new List<string>(new string[]
            {
                "ca",
                "poi"
            });

            ICategoriesRepository pgrsRep = container.Resolve<ICategoriesRepository>("pgrscatrepo");

            foreach (string cat in pgrsRep.GetAll())
                Assert.IsTrue(types.Remove(cat.ToLower()));

            Assert.IsTrue(types.Count == 0);
        }
    }
}
