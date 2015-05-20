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
        public static ILocationsRepository Locations { get; private set; }

        public static IPhotosRepository Photos { get; private set; }

        public static IOfferingsRepository Offerings { get; private set; }

        static DataRepos()
        {
            var connectionString = System.Configuration.ConfigurationManager.ConnectionStrings["PostgresqlCS"].ConnectionString;
            Locations = new PostgresqlLocationsRepository(connectionString);
            var connection = (Locations as PostgresqlLocationsRepository).Connection;
            Photos = new PostgresqlPhotosRepository(connection);
            Offerings = new PostgresqlOfferingsRepository(connection);
            if (connection.State != System.Data.ConnectionState.Open)
                throw new ApplicationException(connection.State.ToString());
        }
    }
}
