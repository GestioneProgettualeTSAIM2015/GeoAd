using GeoAdServer.Domain.Contracts;
using Npgsql;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Postgresql
{
    public class PostgresqlCategoriesRepository : AbstractPostgresqlRepository, ICategoriesRepository
    {
        public PostgresqlCategoriesRepository(string connectionString) : base(connectionString)
        { }

        public PostgresqlCategoriesRepository(NpgsqlConnection connection)  : base(connection)
        { }

        IEnumerable<string> ICategoriesRepository.GetAll()
        {
            string query = "SELECT \"Name\" FROM public.\"Categories\"";
            return ExecQuery<string>(query, (dr) =>
            {
                return dr.Field<string>("Name");
            });
        }
    }
}
