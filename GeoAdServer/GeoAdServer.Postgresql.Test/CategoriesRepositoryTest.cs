using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using GeoAdServer.Domain.Contracts;

namespace GeoAdServer.Postgresql.Test
{
    [TestClass]
    public class CategoriesRepositoryTest
    {
        [TestMethod]
        public void TestMethod1()
        {
            string connectionString = "Server=localhost;Port=5012;UserId=postgres;Password=admin;Database=GeoAdDb";
            ICategoriesRepository pgrsRep = new PostgresqlCategoriesRepository(connectionString);
            foreach (string cat in pgrsRep.GetAll())
                Console.WriteLine(cat);
        }
    }
}
