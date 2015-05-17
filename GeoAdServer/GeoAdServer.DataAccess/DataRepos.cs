using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using GeoAdServer.Domain.Contracts;
using GeoAdServer.Postgresql;

namespace GeoAdServer.DataAccess
{
    public class DataRepos
    {
        public static ILocationsRepository Locations { get; private set; }

        public static IPhotosRepository Photos { get; private set; }

        public static IOfferingsRepository Offerings { get; private set; }

        static DataRepos()
        {
            string connectionString = "Server=localhost;Port=5012;UserId=postgres;Password=admin;Database=GeoAdDb";
            Locations = new PostgresqlLocationsRepository(connectionString);
            var connection = (Locations as PostgresqlLocationsRepository).Connection;
            Photos = new PostgresqlPhotosRepository(connection);
            Offerings = new PostgresqlOfferingsRepository(connection);
            if (connection.State != System.Data.ConnectionState.Open)
                throw new ApplicationException(connection.State.ToString());
        }
    }
}
