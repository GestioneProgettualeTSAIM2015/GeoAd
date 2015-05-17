using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using Npgsql;
using NpgsqlTypes;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Postgresql
{
    public class PostgresqlPhotosRepository : AbstractPostgresqlRepository, IPhotosRepository
    {
        public PostgresqlPhotosRepository(string connectionString) : base(connectionString)
        { }

        public PostgresqlPhotosRepository(NpgsqlConnection connection) : base(connection)
        { }

        IEnumerable<PhotoDTO> IPhotosRepository.GetByLocationId(int locationId)
        {
            string query = @"SELECT ""Id"", ""Data"", ""Width"", ""Height""
                             FROM public.""Photos""
                             WHERE ""LocationId"" = " + locationId;

            return ExecQuery<PhotoDTO>(query, (dr) =>
            {
                return new PhotoDTO
                {
                    Id = dr.Field<int>("Id"),
                    LocationId = locationId,
                    Data = dr.Field<byte[]>("Data"),
                    Width = dr.Field<int>("Width"),
                    Height = dr.Field<int>("Height")
                };
            });
        }

        PhotoDTO IPhotosRepository.GetById(int photoId)
        {
            string query = @"SELECT ""LocationId"", ""Data"", ""Width"", ""Height""
                             FROM public.""Photos""
                             WHERE ""Id"" = " + photoId;

            return ExecQuery<PhotoDTO>(query, (dr) =>
            {
                return new PhotoDTO
                {
                    Id = photoId,
                    LocationId = dr.Field<int>("LocationId"),
                    Data = dr.Field<byte[]>("Data"),
                    Width = dr.Field<int>("Width"),
                    Height = dr.Field<int>("Height")
                };
            }).ElementAtOrDefault(0);
        }

        int IPhotosRepository.Insert(Photo photo)
        {
            var templateCommand = @"INSERT INTO
                                   public.""Photos"" (""Id"", ""LocationId"", ""Data"", ""Width"", ""Height"")
                                   VALUES (DEFAULT, {0}, :bytesData, {1}, {2})
                                   RETURNING ""Id""";

            NpgsqlParameter bytesData = new NpgsqlParameter(":bytesData", NpgsqlDbType.Bytea);
            bytesData.Value = photo.Data;

            object row = ExecCommand(string.Format(templateCommand,
                photo.LocationId, photo.Width, photo.Height), bytesData);

            return (int) row;
        }

        bool IPhotosRepository.Update(int id, Photo photo)
        {
            var templateCommand = @"UPDATE public.""Photos""
                                    SET ""LocationId"" = {0},
                                        ""Data"" = :bytesData,
                                        ""Width"" = {1},
                                        ""Height"" = {2}
                                    WHERE ""Id"" = {3}
                                    RETURNING ""Id""";

            NpgsqlParameter bytesData = new NpgsqlParameter(":bytesData", NpgsqlDbType.Bytea);
            bytesData.Value = photo.Data;

            object row = ExecCommand(string.Format(templateCommand,
                photo.LocationId, photo.Width, photo.Height, id), bytesData);

            return row != null && (int)row == id;
        }

        bool IPhotosRepository.Delete(int photoId)
        {
            var templateCommand = @"DELETE FROM public.""Photos""
                                    WHERE ""Id"" = {0}";

            object row = ExecCommand(string.Format(templateCommand, photoId));
            return true;
        }
    }
}
