using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using Npgsql;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Postgresql
{
    public class PostgresqlLocationsRepository : AbstractPostgresqlRepository, ILocationsRepository
    {
        public Dictionary<int, string> Categories { get;  private set; }

        public PostgresqlLocationsRepository(string connectionString) : base(connectionString)
        {
            Categories = FetchCategories();
        }

        public PostgresqlLocationsRepository(NpgsqlConnection connection) : base(connection)
        {
            Categories = FetchCategories();
        }

        IEnumerable<LocationDTO> ILocationsRepository.GetAll()
        {
            string query = @"SELECT *
                             FROM public.""Locations""";

            return ExecQuery<LocationDTO>(query, (dr) =>
            {
                string pCat, sCat = null;

                int? sCatId = dr.Field<int?>("SCatId");

                Categories.TryGetValue(dr.Field<int>("PCatId"), out pCat);
                if (sCatId.HasValue) Categories.TryGetValue(sCatId.Value, out sCat);

                return new LocationDTO
                {
                    Id = dr.Field<int>("Id"),
                    PCat = pCat,
                    SCat = sCat,
                    Name = dr.Field<string>("Name"),
                    Lat = dr.Field<string>("Lat"),
                    Lng = dr.Field<string>("Lng"),
                    Desc = dr.Field<string>("Desc"),
                    Type = dr.Field<string>("Type")
                };
            });
        }

        IEnumerable<LocationDTO> ILocationsRepository.GetByUserId(string userId)
        {
            string templateQuery = @"SELECT *
                             FROM public.""Locations""
                             WHERE ""UserId"" = '{0}'";

            return ExecQuery<LocationDTO>(string.Format(templateQuery, userId), (dr) =>
            {
                string pCat, sCat = null;

                int? sCatId = dr.Field<int?>("SCatId");

                Categories.TryGetValue(dr.Field<int>("PCatId"), out pCat);
                if (sCatId.HasValue) Categories.TryGetValue(sCatId.Value, out sCat);

                return new LocationDTO
                {
                    Id = dr.Field<int>("Id"),
                    PCat = pCat,
                    SCat = sCat,
                    Name = dr.Field<string>("Name"),
                    Lat = dr.Field<string>("Lat"),
                    Lng = dr.Field<string>("Lng"),
                    Desc = dr.Field<string>("Desc"),
                    Type = dr.Field<string>("Type")
                };
            });
        }

        LocationDTO ILocationsRepository.GetById(int locationId)
        {
            string query = @"SELECT *
                             FROM public.""Locations""
                             WHERE ""Id"" = " + locationId;

            return ExecQuery<LocationDTO>(query, (dr) =>
            {
                string pCat, sCat = null;

                int? sCatId = dr.Field<int?>("SCatId");

                Categories.TryGetValue(dr.Field<int>("PCatId"), out pCat);
                if (sCatId.HasValue) Categories.TryGetValue(sCatId.Value, out sCat);

                return new LocationDTO
                {
                    Id = locationId,
                    PCat = pCat,
                    SCat = sCat,
                    Name = dr.Field<string>("Name"),
                    Lat = dr.Field<string>("Lat"),
                    Lng = dr.Field<string>("Lng"),
                    Desc = dr.Field<string>("Desc"),
                    Type = dr.Field<string>("Type")
                };
            }).ElementAtOrDefault(0);
        }

        int ILocationsRepository.Insert(Location location)
        {
            var templateCommand = @"INSERT INTO
                                   ""Locations""(""Id"", ""UserId"", ""PCatId"", ""SCatId"", ""Name"", ""Lat"", ""Lng"", ""Desc"", ""Type"")
                                   VALUES (DEFAULT, '{0}', {1}, {2}, '{3}', '{4}', '{5}', '{6}', '{7}')
                                   RETURNING ""Id""";

            object row = ExecCommand(string.Format(new NullFormat(), templateCommand,
                location.UserId, location.PCatId, location.SCatId,
                location.Name, location.Lat, location.Lng, location.Desc, location.Type));

            return (int)row;
        }

        void ILocationsRepository.Update(int id, Location location)
        {
            var templateCommand = @"UPDATE ""Locations""
                                    SET ""UserId"" = '{0}',
                                        ""PCatId"" = {1},
                                        ""SCatId"" = {2},
                                        ""Name"" = '{3}',
                                        ""Lat"" = '{4}', 
                                        ""Lng"" = '{5}',
                                        ""Desc"" = '{6}',
                                        ""Type"" = '{7}'
                                    WHERE ""Id"" = {8}";

            object row = ExecCommand(string.Format(new NullFormat(), templateCommand,
                location.UserId, location.PCatId, location.SCatId, location.Name, location.Lat, location.Lng, location.Desc, location.Type, id));
        }

        bool ILocationsRepository.DeleteById(int locationId)
        {
            var templateCommand = @"DELETE FROM public.""Locations""
                                    WHERE ""Id"" = {0}";

            object row = ExecCommand(string.Format(templateCommand, locationId));
            return true;
        }

        int ILocationsRepository.InsertCategory(string name)
        {
            if (Categories.ContainsValue(name)) return -1;

            var templateCommand = @"INSERT INTO
                                   ""Categories""(""Id"", ""Name"")
                                   VALUES (DEFAULT, '{0}')
                                   RETURNING ""Id""";

            object row = ExecCommand(string.Format(templateCommand, name));

            Categories.Add((int)row, name);

            return (int)row;
        }

        bool ILocationsRepository.DeleteCategory(string name)
        {
            if (!Categories.ContainsValue(name)) return false;

            string commandTemplate = @"DELETE FROM ""Categories""
                                       WHERE ""Name"" = '{0}'
                                       RETURNING ""Id""";

            object row = ExecCommand(string.Format(commandTemplate, name));
            Categories.Remove((int)row);

            return true;
        }

        Dictionary<int, string> ILocationsRepository.GetCategories()
        {
            return Categories;
        }

        Dictionary<int, string> FetchCategories()
        {
            string query = @"SELECT ""Id"", ""Name""
                             FROM public.""Categories""";

            Dictionary<int, string> dictionary = new Dictionary<int, string>();
            foreach (var pair in ExecQuery<KeyValuePair<int, string>>(query, (dr) =>
            {
                return new KeyValuePair<int, string>(dr.Field<int>("Id"), dr.Field<string>("Name"));
            }))
            {
                dictionary.Add(pair.Key, pair.Value);
            }

            return dictionary;
        }
    }
}
