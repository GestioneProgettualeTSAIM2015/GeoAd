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
    public class PostgresqOffersRepository : AbstractPostgresqlRepository, IOffersRepository
    {
        public PostgresqOffersRepository(string connectionString) : base(connectionString)
        {
        }

        public PostgresqOffersRepository(NpgsqlConnection connection) : base(connection)
        {
        }

        IEnumerable<OfferDTO> IOffersRepository.GetAll(long? currentUTCMillis = null)
        {
            string query = @"SELECT *
                             FROM ""Offers""";

            if (currentUTCMillis.HasValue)
                query += @" WHERE ""ExpDateMillis"" >= " + currentUTCMillis.Value;

            return ExecQuery<OfferDTO>(query, (dr) =>
            {
                return new OfferDTO
                {
                    Id = dr.Field<int>("Id"),
                    Name = dr.Field<string>("Name"),
                    LocationId = dr.Field<int>("LocationId"),
                    Desc = dr.Field<string>("Desc"),
                    InsDateMillis = dr.Field<long>("InsDateMillis"),
                    ExpDateMillis = dr.Field<long>("ExpDateMillis")
                };
            });
        }

        IEnumerable<OfferDTO> IOffersRepository.GetByLocationId(int locationId, long? currentUTCMillis = null)
        {
            string query = @"SELECT *
                             FROM ""Offers""
                             WHERE ""LocationId"" = {0}";

            if (currentUTCMillis.HasValue)
                query += @" AND ""ExpDateMillis"" >= " + currentUTCMillis.Value;

            return ExecQuery<OfferDTO>(string.Format(query, locationId), (dr) =>
            {
                return new OfferDTO
                {
                    Id = dr.Field<int>("Id"),
                    Name = dr.Field<string>("Name"),
                    LocationId = locationId,
                    Desc = dr.Field<string>("Desc"),
                    InsDateMillis = dr.Field<long>("InsDateMillis"),
                    ExpDateMillis = dr.Field<long>("ExpDateMillis")
                };
            });
        }

        OfferDTO IOffersRepository.GetById(int offerId)
        {
            string query = @"SELECT *
                             FROM ""Offers""
                             WHERE ""Id"" = {0}";

            return ExecQuery<OfferDTO>(string.Format(query, offerId), (dr) =>
            {
                return new OfferDTO
                {
                    Id = offerId,
                    Name = dr.Field<string>("Name"),
                    LocationId = dr.Field<int>("LocationId"),
                    Desc = dr.Field<string>("Desc"),
                    InsDateMillis = dr.Field<long>("InsDateMillis"),
                    ExpDateMillis = dr.Field<long>("ExpDateMillis")
                };
            }).ElementAtOrDefault(0);
        }

        int IOffersRepository.Insert(Offer offer)
        {
            var templateCommand = @"INSERT INTO
                                   ""Offers"" (""Id"", ""LocationId"", ""Desc"", ""InsDateMillis"", ""ExpDateMillis"", ""Name"")
                                   VALUES (DEFAULT, {0}, '{1}', {2}, {3}, '{4}')
                                   RETURNING ""Id""";

            object row = ExecCommand(string.Format(new NullFormat(), templateCommand,
                offer.LocationId, offer.Desc,
                (long) (DateTime.Now - new DateTime(1970, 1, 1)).TotalMilliseconds, offer.ExpDateMillis, offer.Name));

            return (int)row;
        }

        bool IOffersRepository.Update(int id, Offer offer)
        {
            var templateCommand = @"UPDATE public.""Offers""
                                    SET ""LocationId"" = {0},
                                        ""Desc"" = '{1}',
                                        ""ExpDateMillis"" = {2},
                                        ""Name"" = '{3}'
                                    WHERE ""Id"" = {4}
                                    RETURNING ""Id""";

            object row = ExecCommand(string.Format(templateCommand,
                offer.LocationId, offer.Desc, offer.ExpDateMillis, offer.Name, id));

            return row != null && (int)row == id;
        }

        bool IOffersRepository.DeleteById(int offerId)
        {
            var templateCommand = @"DELETE FROM public.""Offers""
                                    WHERE ""Id"" = {0}";

            object row = ExecCommand(string.Format(templateCommand, offerId));
            return true;
        }

        bool IOffersRepository.DeleteByLocationId(int locationId)
        {
            var templateCommand = @"DELETE FROM public.""Offers""
                                    WHERE ""LocationId"" = {0}
                                    RETURNING count";

            object row = ExecCommand(string.Format(templateCommand, locationId));
            return true;
        }
    }
}
