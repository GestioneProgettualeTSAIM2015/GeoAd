using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using GeoAdServer.Domain.Contracts;
using GeoAdServer.Postgresql;
using System.Configuration;
using System.IO;
using System.Reflection;

namespace GeoAdServer.DataAccess
{
    public class DataRepos
    {
        public static ILocationsRepository Locations
        {
            get
            {
                return new PostgresqlLocationsRepository(connectionString);
            }
        }

        public static IPhotosRepository Photos
        {
            get
            {
                return new PostgresqlPhotosRepository(connectionString);
            }
        }

        public static IOfferingsRepository Offerings
        {
            get
            {
                return new PostgresqlOfferingsRepository(connectionString);
            }
        }

        private readonly static string connectionString = System.Configuration.ConfigurationManager.ConnectionStrings["PostgresqlCS"].ConnectionString;
    }
}