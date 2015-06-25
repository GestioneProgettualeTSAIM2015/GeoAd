using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities;
using Npgsql;
using NpgsqlTypes;
using System;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Postgresql
{
    public class PostgresqPreferencesRepository : AbstractPostgresqlRepository, IPreferencesRepository
    {
        public PostgresqPreferencesRepository(string connectionString) : base(connectionString)
        { }

        public PostgresqPreferencesRepository(NpgsqlConnection connection) : base(connection)
        { }

        Preferences IPreferencesRepository.Get()
        {
            string query = @"SELECT ""Data""
                             FROM ""UserPreferences""
                             WHERE ""PrefName"" = 'FavIgn'";

            try
            {
                using (var reader = ExecReader(query))
                {
                    if (reader.Read())
                    {
                        byte[] result = (byte[])reader[0];
                        return result.ToPreference();
                    }
                }
            }
            catch
            { }

            return new Preferences();
        }

        bool IPreferencesRepository.Set(Preferences prefs)
        {
            try
            {
                string queryTemplate = @"UPDATE ""UserPreferences""
                                         SET ""Data"" = :dataFavIgn
                                         WHERE ""PrefName"" = 'FavIgn'";

                NpgsqlParameter param = new NpgsqlParameter("dataFavIgn", NpgsqlDbType.Bytea);
                param.Value = prefs.ToByteArray();

                object row = ExecCommand(queryTemplate, param);
                return true;
            }
            catch
            { }

            return false;
        }
    }

    public static class PreferencesUtils
    {
        public static byte[] ToByteArray(this Preferences command)
        {
            if (command == null)
                return null;

            BinaryFormatter bf = new BinaryFormatter();
            MemoryStream ms = new MemoryStream();
            bf.Serialize(ms, command);
            return ms.ToArray();
        }

        public static Preferences ToPreference(this byte[] arrBytes)
        {
            MemoryStream memStream = new MemoryStream();
            BinaryFormatter binForm = new BinaryFormatter();
            memStream.Write(arrBytes, 0, arrBytes.Length);
            memStream.Seek(0, SeekOrigin.Begin);
            Preferences command = (Preferences)binForm.Deserialize(memStream);
            return command;
        }
    }
}
