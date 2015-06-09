﻿using GeoAdServer.Domain.Contracts;
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
        private static Dictionary<string, IList<string>> _categories;

        public static Dictionary<string, IList<string>> Categories
        {
            get
            {
                if (_categories == null) return _categories;

                lock (_categories)
                {
                    return _categories;
                }
            }

            private set
            {
                if (_categories != null)
                {
                    lock (_categories)
                    {
                        _categories = value;
                    }
                }
                else
                {
                    _categories = value;
                }
            }
        }

        private static DataTable _categoriesDataTable;

        private static DataTable CategoriesDataTable
        {
            get
            {
                if (_categories == null) return _categoriesDataTable;

                lock (_categories)
                {
                    return _categoriesDataTable;
                }
            }

            set
            {
                if (_categories != null)
                {
                    lock (_categories)
                    {
                        _categoriesDataTable = value;
                    }
                }
                else
                {
                    _categoriesDataTable = value;
                }
            }
        }

        public PostgresqlLocationsRepository(string connectionString) : base(connectionString)
        {
            if (Categories == null) Categories = FetchCategories();
        }

        public PostgresqlLocationsRepository(NpgsqlConnection connection) : base(connection)
        {
            if (Categories == null) Categories = FetchCategories();
        }

        IEnumerable<LocationDTO> ILocationsRepository.GetAll()
        {
            string query = @"SELECT *
                             FROM public.""Locations""";

            return ExecQuery<LocationDTO>(query, (dr) =>
            {
                string pCat, sCat = null;

                int? sCatId = dr.Field<int?>("SCatId");

                pCat = CategoriesDataTable.Rows.Find(dr.Field<int>("PCatId")).Field<string>("Name");
                if (sCatId.HasValue) sCat = CategoriesDataTable.Rows.Find(sCatId.Value).Field<string>("Name");

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

                pCat = CategoriesDataTable.Rows.Find(dr.Field<int>("PCatId")).Field<string>("Name");
                if (sCatId.HasValue) sCat = CategoriesDataTable.Rows.Find(sCatId.Value).Field<string>("Name");

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

                pCat = CategoriesDataTable.Rows.Find(dr.Field<int>("PCatId")).Field<string>("Name");
                if (sCatId.HasValue) sCat = CategoriesDataTable.Rows.Find(sCatId.Value).Field<string>("Name");

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

        string ILocationsRepository.GetOwnerId(int locationId)
        {
            string query = @"SELECT ""UserId""
                             FROM public.""Locations""
                             WHERE ""Id"" = " + locationId;

            return ExecQuery<string>(query, (dr) =>
            {
                return dr.Field<string>("UserId");
            }).SingleOrDefault();
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

        bool ILocationsRepository.Update(int id, Location location)
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
                                    WHERE ""Id"" = {8}
                                    RETURNING ""Id""";

            object row = ExecCommand(string.Format(new NullFormat(), templateCommand,
                location.UserId, location.PCatId, location.SCatId, location.Name, location.Lat, location.Lng, location.Desc, location.Type, id));

            return row != null && (int)row == id;
        }

        bool ILocationsRepository.DeleteById(int locationId)
        {
            var templateCommand = @"DELETE FROM public.""Locations""
                                    WHERE ""Id"" = {0}";

            object row = ExecCommand(string.Format(templateCommand, locationId));
            return true;
        }

        int ILocationsRepository.InsertCategory(string name, int? aggregate)
        {
            if (CategoriesDataTable.Select("Name = '" + name + "'").Length > 0) return -1;

            var templateCommand = @"INSERT INTO
                                   ""Categories""(""Id"", ""Name"", ""Aggregate"")
                                   VALUES (DEFAULT, '{0}', {1})
                                   RETURNING ""Id""";

            object row = ExecCommand(string.Format(new NullFormat(), templateCommand, name, aggregate));

            CategoriesDataTable.Rows.Add((int)row, name, aggregate);

            if (!aggregate.HasValue)
                Categories.Add(name, new List<string>());
            else
            {
                string primaryCategoryName = CategoriesDataTable.Rows.Find(aggregate.Value).Field<string>("Name");

                IList<string> secondaryCategories;

                if (Categories.TryGetValue(primaryCategoryName, out secondaryCategories))
                {
                    secondaryCategories.Add(name);
                }
            }

            return (int)row;
        }

        bool ILocationsRepository.DeleteCategory(string name)
        {
            DataRow[] rows;
            if ((rows = CategoriesDataTable.Select("Name = '" + name + "'")).Length == 0) return false;

            string commandTemplate = @"DELETE FROM ""Categories""
                                       WHERE ""Name"" = '{0}'
                                       RETURNING ""Id""";

            object row = ExecCommand(string.Format(commandTemplate, name));

            rows.Single().Delete();

            if (Categories.ContainsKey(name))
            {
                Categories.Remove(name);
            }
            else
            {
                foreach (var pair in Categories)
                {
                    var sCats = pair.Value;
                    if (sCats.Contains(name))
                    {
                        sCats.Remove(name);
                        break;
                    }
                }
            }

            return true;
        }

        Dictionary<string, IList<string>> ILocationsRepository.GetCategories()
        {
            return Categories;
        }

        Dictionary<string, IList<string>> FetchCategories()
        {
            string query = @"SELECT ""Id"", ""Name"", ""Aggregate""
                             FROM public.""Categories""";

            InitCategoriesDataTable();

            foreach (var cat in ExecQuery<CategoryDTO>(query, (dr) =>
            {
                return new CategoryDTO
                {
                    Id = dr.Field<int>("Id"),
                    Name = dr.Field<string>("Name"),
                    Aggregate = dr.Field<int?>("Aggregate")
                };
            }))
            {
                if (cat.Aggregate.HasValue) CategoriesDataTable.Rows.Add(cat.Id, cat.Name, cat.Aggregate.Value);
                else CategoriesDataTable.Rows.Add(cat.Id, cat.Name, DBNull.Value);
            }

            Dictionary<string, IList<string>> dictionary = new Dictionary<string, IList<string>>();

            foreach (DataRow row in CategoriesDataTable.Rows)
            {
                if (!row.Field<int?>("Aggregate").HasValue)
                    dictionary.Add(row.Field<string>("Name"), new List<string>());
                else
                {
                    string primaryCategoryName = CategoriesDataTable.Rows.Find(
                        row.Field<int?>("Aggregate").Value).Field<string>("Name");

                    IList<string> secondaryCategories;

                    if (dictionary.TryGetValue(primaryCategoryName, out secondaryCategories))
                    {
                        secondaryCategories.Add(row.Field<string>("Name"));
                    }
                }
            }

            return dictionary;
        }

        int? ILocationsRepository.GetCategoryId(string name)
        {
            var row = CategoriesDataTable.Select("Name = '" + name + "'").FirstOrDefault();
            if (row == null) return null;

            return row.Field<int>("Id");
        }

        void InitCategoriesDataTable()
        {
            CategoriesDataTable = new DataTable();
            CategoriesDataTable.Columns.Add("Id", typeof(int));
            CategoriesDataTable.Columns.Add("Name", typeof(string));

            DataColumn aggregateColumn;
            aggregateColumn = new DataColumn("Aggregate", typeof(int));
            aggregateColumn.AllowDBNull = true;
            CategoriesDataTable.Columns.Add(aggregateColumn);

            CategoriesDataTable.PrimaryKey = new DataColumn[] { CategoriesDataTable.Columns["Id"] };
        }
    }
}
