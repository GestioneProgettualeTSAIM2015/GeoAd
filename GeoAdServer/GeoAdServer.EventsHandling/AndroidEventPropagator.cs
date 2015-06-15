using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities.Events;
using Newtonsoft.Json;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.ComponentModel;
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
    public class AndroidEventPropagator : IEventsHandler
    {
        private readonly static string _applicationId =
            ConfigurationManager.AppSettings["androidApplicationId"];

        IChangedPositionHandler IEventsHandler.ChpHandler { get; set; }

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

        void IEventsHandler.Enqueue(IEvent command)
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
                    else if (ievent is OfferingCreated)
                    {
                        Handle(ievent as OfferingCreated);
                    }
                    else if (ievent is OfferingUpdated)
                    {
                        Handle(ievent as OfferingUpdated);
                    }
                    else if (ievent is OfferingDeleted)
                    {
                        Handle(ievent as OfferingDeleted);
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

        private void Handle(OfferingCreated ievent)
        {
            NotifyAffected("OfferingCreated", ievent.Offering
                .AddProperty("LocationName", ievent.LocationName), ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferingUpdated ievent)
        {
            NotifyAffected("OfferingUpdated", ievent.Offering
                .AddProperty("LocationName", ievent.LocationName), ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferingDeleted ievent)
        {
            NotifyAffected("OfferingDeleted", ievent.OfferingId, ievent.Lat, ievent.Lng);
        }

        private void NotifyAffected(string action, object dataObject, string lat, string lng)
        {
            IChangedPositionHandler chpHandler = ((IEventsHandler)this).ChpHandler;
            if (chpHandler != null)
            {
                IEnumerable<string> keys = chpHandler.GetKeysAffected(
                    double.Parse(lat, CultureInfo.InvariantCulture), double.Parse(lng, CultureInfo.InvariantCulture));

                Push(action, dataObject, keys);
            }
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
                request.Headers.Add(string.Format("Authorization: key={0}", _applicationId));

                Byte[] byteArray = Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(push));
                request.ContentLength = byteArray.Length;

                using (Stream pushStream = request.GetRequestStream())
                    pushStream.Write(byteArray, 0, byteArray.Length);

                Log(string.Format("pushed {0} to {1} apps", action, keys.Count()));

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
        }
    }

    internal class Logger
    {
        private string _logDirPath;

        public string LogDirPath {
            get
            {
                return _logDirPath;
            }

            set
            {
                if (value == null) return;

                if (!Directory.Exists(value))
                    Directory.CreateDirectory(value);

                _logDirPath = value;
                _logFilePath = string.Format("{0}\\{1}.logs", value, DateTime.Now.ToString("yyyyMMdd"));
            }
        }

        private string _logFilePath;

        public async void Log(string log)
        {
            if (_logFilePath == null) return;

            using (StreamWriter outfile = new StreamWriter(_logFilePath, true))
            {
                await outfile.WriteAsync(string.Format("[{0}] {1}\n", DateTime.Now.ToString(), log));
            }
        }
    }

    internal static class Utils
    {
        private static Random random = new Random((int)DateTime.Now.Ticks);

        public static string BuildRandomString(int size)
        {
            StringBuilder builder = new StringBuilder();
            char ch;
            for (int i = 0; i < size; i++)
            {
                ch = Convert.ToChar(Convert.ToInt32(Math.Floor(26 * random.NextDouble() + 97)));
                builder.Append(ch);
            }

            return builder.ToString();
        }

        public static dynamic AddProperty(this object obj, string newPropertyName, object newPropertyValue)
        {
            var type = obj.GetType();
            var newObj = new ExpandoObject() as IDictionary<string, object>;

            foreach (PropertyDescriptor property in TypeDescriptor.GetProperties(obj.GetType()))
                newObj.Add(property.Name, property.GetValue(obj));

            newObj.Add(newPropertyName, newPropertyValue);

            return newObj as ExpandoObject;
        }
    }
}
