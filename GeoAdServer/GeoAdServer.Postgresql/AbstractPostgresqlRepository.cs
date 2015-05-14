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

        protected IEnumerable<T> ExecQuery<T>(string query, Func<DataRow, T> rp)
        {
            DataTable dt = Exec(query);

            foreach (DataRow dr in dt.Rows)
                yield return rp(dr);
        }

        protected object ExecCommand(string query)
        {
            NpgsqlCommand cmd = new NpgsqlCommand(query, Connection);
            return cmd.ExecuteScalar();
        }

        DataTable Exec(string query)
        {
            DataSet ds = new DataSet();
            NpgsqlDataAdapter da = new NpgsqlDataAdapter(query, Connection);
            ds.Reset();
            da.Fill(ds);
            int tables = ds.Tables.Count;
            return ds.Tables[0];
        }

        public void Dispose()
        {
            Connection.Close();
        }
    }
}
