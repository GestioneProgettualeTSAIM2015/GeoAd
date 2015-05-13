using Npgsql;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Postgresql
{
    public abstract class AbstractPostgresqlRepository : IDisposable
    {
        protected NpgsqlConnection Connection { get; private set; }

        public AbstractPostgresqlRepository(string connectionString) : this(new NpgsqlConnection(connectionString))
        {
        }

        public AbstractPostgresqlRepository(NpgsqlConnection connection)
        {
            Connection = connection;
            Connection.Open();
        }

        protected IEnumerable<T> ExecQuery<T>(string query, ResultParser<T> rp)
        {
            DataSet ds = new DataSet();
            DataTable dt;
            NpgsqlDataAdapter da = new NpgsqlDataAdapter(query, Connection);
            ds.Reset();
            da.Fill(ds);
            int tables = ds.Tables.Count;
            dt = ds.Tables[0];

            foreach (DataRow dr in dt.Rows)
                yield return rp(dr);
        }

        public void Dispose()
        {
            Connection.Close();
        }

        protected delegate T ResultParser<T>(DataRow dr);
    }
}
