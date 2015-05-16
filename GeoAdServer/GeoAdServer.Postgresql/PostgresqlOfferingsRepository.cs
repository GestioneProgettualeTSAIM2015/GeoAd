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
    public class PostgresqlOfferingsRepository : AbstractPostgresqlRepository, IOfferingsRepository
    {
        public PostgresqlOfferingsRepository(string connectionString) : base(connectionString)
        {
        }

        public PostgresqlOfferingsRepository(NpgsqlConnection connection) : base(connection)
        {
        }

        IEnumerable<OfferingDTO> IOfferingsRepository.GetAll()
        {
            string query = @"SELECT *
                             FROM ""Offerings""";

            return ExecQuery<OfferingDTO>(query, (dr) =>
            {
                return new OfferingDTO
                {
                    Id = dr.Field<int>("Id"),
                    LocationId = dr.Field<int>("LocationId"),
                    Desc = dr.Field<string>("Desc"),
                    InsDateMillis = dr.Field<long>("InsDateMillis"),
                    ExpDateMillis = dr.Field<long>("ExpDateMillis")
                };
            });
        }

        IEnumerable<OfferingDTO> IOfferingsRepository.GetByLocationId(int locationId)
        {
            string query = @"SELECT *
                             FROM ""Offerings""
                             WHERE ""LocationId"" = {0}";

            return ExecQuery<OfferingDTO>(string.Format(query, locationId), (dr) =>
            {
                return new OfferingDTO
                {
                    Id = dr.Field<int>("Id"),
                    LocationId = locationId,
                    Desc = dr.Field<string>("Desc"),
                    InsDateMillis = dr.Field<long>("InsDateMillis"),
                    ExpDateMillis = dr.Field<long>("ExpDateMillis")
                };
            });
        }

        int IOfferingsRepository.Insert(Offering offering)
        {
            var templateCommand = @"INSERT INTO
                                   ""Offerings"" (""Id"", ""LocationId"", ""Desc"", ""InsDateMillis"", ""ExpDateMillis"")
                                   VALUES (DEFAULT, {0}, '{1}', {2}, {3})
                                   RETURNING ""Id""";

            object row = ExecCommand(string.Format(new NullFormat(), templateCommand,
                offering.LocationId, offering.Desc,
                (long) (DateTime.Now - new DateTime(1970, 1, 1)).TotalMilliseconds, offering.ExpDateMillis));

            return (int)row;
        }

        void IOfferingsRepository.Update(int id, Offering offering)
        {
            var templateCommand = @"UPDATE public.""Offerings""
                                    SET ""LocationId"" = {0},
                                        ""Desc"" = '{1}',
                                        ""ExpDateMillis"" = {2}
                                    WHERE ""Id"" = {3}";

            object row = ExecCommand(string.Format(templateCommand,
                offering.LocationId, offering.Desc, offering.ExpDateMillis, id));
        }

        bool IOfferingsRepository.DeleteById(int offeringId)
        {
            var templateCommand = @"DELETE FROM public.""Offerings""
                                    WHERE ""Id"" = {0}";

            object row = ExecCommand(string.Format(templateCommand, offeringId));
            return true;
        }
    }
}
