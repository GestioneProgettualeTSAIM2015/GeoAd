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
        public Dictionary<CategoryDTO, IList<CategoryDTO>> Categories { get;  private set; }

        private DataTable CategoriesDataTable { get; set; }

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
            if (CategoriesDataTable.Select("Name = " + name).Length > 0) return -1;

            var templateCommand = @"INSERT INTO
                                   ""Categories""(""Id"", ""Name"", ""Aggregate"")
                                   VALUES (DEFAULT, '{0}', {1})
                                   RETURNING ""Id""";

            object row = ExecCommand(string.Format(new NullFormat(), templateCommand, name, aggregate));

            CategoriesDataTable.Rows.Add((int)row, name, aggregate);

            return (int)row;
        }

        bool ILocationsRepository.DeleteCategory(string name)
        {
            if (CategoriesDataTable.Select("Name = " + name).Length == 0) return false;

            string commandTemplate = @"DELETE FROM ""Categories""
                                       WHERE ""Name"" = '{0}'
                                       RETURNING ""Id""";

            object row = ExecCommand(string.Format(commandTemplate, name));
            CategoriesDataTable.Rows.Find((int)row).Delete();

            return true;
        }

        Dictionary<CategoryDTO, IList<CategoryDTO>> ILocationsRepository.GetCategories()
        {
            return Categories;
        }

        Dictionary<CategoryDTO, IList<CategoryDTO>> FetchCategories()
        {
            string query = @"SELECT ""Id"", ""Name"", ""Aggregate""
                             FROM public.""Categories""";

            Dictionary<CategoryDTO, IList<CategoryDTO>> dictionary =
                new Dictionary<CategoryDTO, IList<CategoryDTO>>(new CategoriesEqualitiComparer());

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

                if (!cat.Aggregate.HasValue)
                    dictionary.Add(cat, new List<CategoryDTO>());
                else
                {
                    IList<CategoryDTO> secondaryCategories;

                    if (dictionary.TryGetValue(new CategoryDTO
                    {
                        Id = cat.Aggregate.Value
                    }, out secondaryCategories))
                    {
                        secondaryCategories.Add(cat);
                    }
                }
            }

            return dictionary;
        }

        int? ILocationsRepository.GetCategoryId(string name)
        {
            var row = CategoriesDataTable.Select("Name = " + name).FirstOrDefault();
            if (row == null) return -1;

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

        class CategoriesEqualitiComparer : IEqualityComparer<CategoryDTO>
        {
            bool IEqualityComparer<CategoryDTO>.Equals(CategoryDTO cat1, CategoryDTO cat2)
            {
                return cat1.Id == cat2.Id;
            }

            int IEqualityComparer<CategoryDTO>.GetHashCode(CategoryDTO cat)
            {
                return cat.Id;
            }
        }
    }
}
