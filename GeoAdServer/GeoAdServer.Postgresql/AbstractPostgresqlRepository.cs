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
        public NpgsqlConnection Connection { get; private set; }

        public AbstractPostgresqlRepository(string connectionString) : this(new NpgsqlConnection(connectionString))
        {
        }

        public AbstractPostgresqlRepository(NpgsqlConnection connection)
        {
            Connection = connection;
            if (Connection.State != ConnectionState.Open)
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

        protected object ExecCommand(string query, params NpgsqlParameter[] queryParameters)
        {
            NpgsqlCommand cmd = new NpgsqlCommand(query, Connection);
            foreach (NpgsqlParameter par in queryParameters)
                cmd.Parameters.Add(par);
            return cmd.ExecuteScalar();
        }

        protected NpgsqlDataReader ExecReader(string query, params NpgsqlParameter[] queryParameters)
        {
            NpgsqlCommand cmd = new NpgsqlCommand(query, Connection);
            foreach (NpgsqlParameter par in queryParameters)
                cmd.Parameters.Add(par);
            return cmd.ExecuteReader();
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

        protected class NullFormat : IFormatProvider, ICustomFormatter
        {
            public object GetFormat(Type service)
            {
                if (service == typeof(ICustomFormatter))
                {
                    return this;
                }
                else
                {
                    return null;
                }
            }

            public string Format(string format, object arg, IFormatProvider provider)
            {
                if (arg == null)
                {
                    return "NULL";
                }
                IFormattable formattable = arg as IFormattable;
                if (formattable != null)
                {
                    return formattable.ToString(format, provider);
                }
                return arg.ToString();
            }
        }
    }
}
