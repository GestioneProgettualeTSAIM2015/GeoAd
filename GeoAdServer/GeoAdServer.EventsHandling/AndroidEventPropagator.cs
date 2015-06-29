using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities.Events;
using GeoAdServer.GeneralUtilities;
using Newtonsoft.Json;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Configuration;
using System.Dynamic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Reflection;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace GeoAdServer.EventsHandling
{
    public class AndroidEventPropagator : AbstractEventsHandler
    {
        private readonly static string _APPLICATION_ID =
            ConfigurationManager.AppSettings["androidApplicationId"];

        public bool IsWorking { get; private set; }

        private static ConcurrentQueue<IEvent> _eventsQueue;

        public string Id { get; private set; }

        private Logger _logger;

        public AndroidEventPropagator(string logDirPath)
        {
            if (_eventsQueue == null) _eventsQueue = new ConcurrentQueue<IEvent>();

            _logger = new Logger()
            {
                LogDirPath = logDirPath
            };

            Id = Utils.BuildRandomString(6);

            Log("new AndroidEventPropagator");
        }

        private void Log(string message)
        {
            _logger.Log(Id + " => " + message);
        }

        public override void Enqueue(IEvent command)
        {
            _eventsQueue.Enqueue(command);
            if (!IsWorking)
            {
                IsWorking = true;
                new Thread(Process).Start();
            }
        }

        private void Process()
        {
            Log("aep starts running");
            while (IsWorking)
            {
                IEvent ievent;
                _eventsQueue.TryDequeue(out ievent);
                if (ievent != null)
                {
                    if (ievent is LocationCreated)
                    {
                        Handle(ievent as LocationCreated);
                    }
                    else if (ievent is LocationUpdated)
                    {
                        Handle(ievent as LocationUpdated);
                    }
                    else if (ievent is LocationDeleted)
                    {
                        Handle(ievent as LocationDeleted);
                    }
                    else if (ievent is OfferCreated)
                    {
                        Handle(ievent as OfferCreated);
                    }
                    else if (ievent is OfferUpdated)
                    {
                        Handle(ievent as OfferUpdated);
                    }
                    else if (ievent is OfferDeleted)
                    {
                        Handle(ievent as OfferDeleted);
                    }
                }
                else
                {
                    IsWorking = false;
                    Log("aep stopped");
                }
            }
        }

        private void Handle(LocationCreated ievent)
        {
            NotifyAffected("LocationCreated", ievent.Location, ievent.Location.Lat, ievent.Location.Lng);
        }

        private void Handle(LocationUpdated ievent)
        {
            NotifyAffected("LocationUpdated", ievent.Location, ievent.Location.Lat, ievent.Location.Lng);
        }

        private void Handle(LocationDeleted ievent)
        {
            NotifyAffected("LocationDeleted", ievent.LocationId, ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferCreated ievent)
        {
            NotifyAffected("OfferCreated", ievent.Offer
                .AddProperty("LocationName", ievent.LocationName), ievent.Lat, ievent.Lng, ievent.LocationId);
        }

        private void Handle(OfferUpdated ievent)
        {
            NotifyAffected("OfferUpdated", ievent.Offer
                .AddProperty("LocationName", ievent.LocationName), ievent.Lat, ievent.Lng, ievent.LocationId);
        }

        private void Handle(OfferDeleted ievent)
        {
            NotifyAffected("OfferDeleted", ievent.OfferId, ievent.Lat, ievent.Lng, ievent.LocationId);
        }

        private void NotifyAffected(string action, object dataObject, string lat, string lng, int? locationId = null)
        {
            Log(string.Format("event {0} at position: {1},{2}", action, lat, lng));
            Push(action, dataObject,
                    ((IEventsHandler)this).GetKeysAffected(
                        double.Parse(lat, CultureInfo.InvariantCulture),
                        double.Parse(lng, CultureInfo.InvariantCulture),
                        locationId));
        }

        private readonly static int _MAX_KEYS_PER_PUSH = 1000;

        private void Push(string action, dynamic dataObject, IEnumerable<string> keys)
        {
            dynamic push = new ExpandoObject();
            push.time_to_live = 600;
            push.data = new ExpandoObject();
            push.data.action = action;
            push.data.message = dataObject;

            for (int i = 0; i < keys.Count(); i += _MAX_KEYS_PER_PUSH)
            {
                push.registration_ids = keys.Skip(i).Take(_MAX_KEYS_PER_PUSH);
                
                //send
                WebRequest request;
                request = WebRequest.Create("https://android.googleapis.com/gcm/send");
                request.Method = "post";
                request.ContentType = "application/json;charset=UTF-8";
                request.Headers.Add(string.Format("Authorization: key={0}", _APPLICATION_ID));

                Byte[] byteArray = Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(push));
                request.ContentLength = byteArray.Length;

                using (Stream pushStream = request.GetRequestStream())
                    pushStream.Write(byteArray, 0, byteArray.Length);

                //receive
                try
                {
                    using (HttpWebResponse response = request.GetResponse() as HttpWebResponse) {
                        using (StreamReader responseStreamReader = new StreamReader(response.GetResponseStream()))
                        {
                            string s = null;
                            try
                            {
                                s = responseStreamReader.ReadToEnd();
                                JsonConvert.DeserializeObject(s); //Success
                            } catch (JsonSerializationException)
                            {
                                Log(string.Format("push bad response {{{0}}}: \n>>>>>>>>>>\n{1}<<<<<<<<<<",
                                    response.StatusCode.ToString(), s));
                            }
                        }
                    }
                }
                catch (WebException wEx)
                {
                    using (HttpWebResponse response = wEx.Response as HttpWebResponse)
                        using (StreamReader responseStreamReader = new StreamReader(response.GetResponseStream()))
                            Log(string.Format("push error {{{0}}}: \n>>>>>>>>>>\n{1}<<<<<<<<<<",
                                response.StatusCode.ToString(), responseStreamReader.ReadToEnd()));
                }
            }

            Log(string.Format("pushed to {0} apps", keys.Count()));
        }
    }
}
