﻿using System;
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
    public class DataRepos : IDisposable
    {
        public static DataRepos Instance {
            get
            {
                return new DataRepos();
            }
        }

        public ILocationsRepository Locations { get; private set; }

        public IPhotosRepository Photos { get; private set; }

        public IOffersRepository Offers { get; private set; }

        public IPreferencesRepository Preferences { get; private set; }

        private readonly static string _CONNECTION_STRING =
            ConfigurationManager.ConnectionStrings["PostgresqlCS"].ConnectionString;

        private DataRepos()
        {
            Locations = new PostgresqLocationsRepository(_CONNECTION_STRING);
            var connection = ((AbstractPostgresqlRepository)Locations).Connection;
            Photos = new PostgresqPhotosRepository(connection);
            Offers = new PostgresqOffersRepository(connection);
            Preferences = new PostgresqPreferencesRepository(connection);
        }

        void IDisposable.Dispose()
        {
            Locations.Dispose();
        }
    }
}
