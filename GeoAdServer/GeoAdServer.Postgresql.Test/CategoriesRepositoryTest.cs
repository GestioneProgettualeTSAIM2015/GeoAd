using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using GeoAdServer.Domain.Contracts;
using System.Collections.Generic;

namespace GeoAdServer.Postgresql.Test
{
    [TestClass]
    public class CategoriesRepositoryTest
    {
        [TestMethod]
        public void TestMethod1()
        {
            IList<string> types = new List<string>(new string[]
            {
                "ca",
                "poi"
            });

            string connectionString = "Server=localhost;Port=5012;UserId=postgres;Password=admin;Database=GeoAdDb";
            ICategoriesRepository pgrsRep = new PostgresqlCategoriesRepository(connectionString);

            foreach (string cat in pgrsRep.GetAll())
                Assert.IsTrue(types.Remove(cat.ToLower()));

            Assert.IsTrue(types.Count == 0);
        }
    }
}
