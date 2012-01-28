package com.proofpoint.anomalytics.rtstats;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.reverse;
import static com.google.common.collect.Lists.transform;

@Path("/v1/ui")
public class UIResource
{
    private final Store store;

    @Inject
    public UIResource(Store store)
    {
        this.store = store;
    }


    @Path("fragment/{key: \\w+}")
    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response getTable(@PathParam("key") String key)
    {
        Pair<Object, Object> entry = store.get(key);

        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("[" + key + "]").build();
        }

        List<Pair<String, Integer>> oldEntries = transform((List<Object>) entry.getFirst(), toLabelCountPair());
        List<Pair<String, Integer>> newEntries = transform((List<Object>) entry.getSecond(), toLabelCountPair());
        /*
            key
            value (to display bar)
            delta rank
                10: new
                1..9: increase
                0: no change
                -1..-9: decrease
         */

        // [ [ "rolex.com", 2487 ], [ "pipethat.com", 2487 ], [ "drugsperu.ru", 1285 ], [ "facebook.com", 1129 ], [ "onlinehealthpharmacy.ru", 806 ], [ "penisproiphone.ru", 777 ], [ "hcg-lossweightweb.ru", 770 ], [ "med-2012pills.ru", 768 ], [ "twitter.com", 743 ], [ "penisfeel.com", 643 ] ]
        // [ [ "rolex.com", 4731 ], [ "pipethat.com", 4731 ], [ "facebook.com", 2786 ], [ "drugsperu.ru", 2631 ], [ "twitter.com", 1873 ], [ "chtah.com", 1345 ], [ "stvin.org", 1332 ], [ "4at5.net", 1196 ], [ "steinmart.com", 1196 ], [ "bytetoyours.net", 1152 ] ]

        StringBuilder builder = new StringBuilder();
        builder.append("<table class=\"condensed-table\">");

        builder.append("    <tbody>");
        
        for (DeltaRecord record : computeDeltas(oldEntries, newEntries)) {
            builder.append("        <tr>");
            builder.append("            <td>");
            if (record.getDelta() >= 5) {
                builder.append("<span class=\"label success\">&#x21c8</span>");
            }
            else if (record.getDelta() > 0) {
                builder.append("<span class=\"label success\">&#x2191</span>");
            }
            else if (record.getDelta() == 0) {
                builder.append("<span class=\"label\">&#x2194</span>");
            }
            else if (record.getDelta() > -5) {
                builder.append("<span class=\"label important\">&#x2193</span>");
            }
            else if (record.getDelta() <= -5) {
                builder.append("<span class=\"label important\">&#x21ca</span>");
            }
            builder.append("            </td>");
            builder.append("            <td>" + record.getLabel() + "</td>");
            builder.append("            <td>" + record.getCount() + "</td>");

            builder.append("            <td>");
            if (record.getDelta() >= newEntries.size()) {
                builder.append("<span class=\"label notice\">new</span>");
            }
            builder.append("            </td>");

            builder.append("        </tr>");
        }
        builder.append("    </tbody>");

        builder.append("</table>");

        return Response.ok(builder.toString()).build();
    }


    private List<DeltaRecord> computeDeltas(List<Pair<String, Integer>> oldEntries, List<Pair<String, Integer>> newEntries) // todo: use pairs
    {
        Map<String, Integer> oldPositions = positionMap(reverse(transform(oldEntries, Pair.<String, Integer>first())));
        Map<String, Integer> newPositions = positionMap(reverse(transform(newEntries, Pair.<String, Integer>first())));

        ImmutableList.Builder<DeltaRecord> builder = ImmutableList.builder();
        for (Pair<String, Integer> pair : newEntries) {
            String label = pair.getFirst();
            Integer count = pair.getSecond();

            int delta = 10;
            Integer oldPosition = oldPositions.get(label);
            if (oldPosition != null) {
                delta = newPositions.get(label) - oldPosition; // TODO: values are sorted to to bottom
            }

            builder.add(new DeltaRecord(delta, label, count));
        }

        return builder.build();
    }

    private static Function<Object, Pair<String, Integer>> toLabelCountPair()
    {
        return new Function<Object, Pair<String, Integer>>()
        {
            public Pair<String, Integer> apply(Object entry)
            {
                List<Object> asList = (List<Object>) entry;

                return new Pair<String, Integer>((String) asList.get(0), ((Number) asList.get(1)).intValue());
            }
        };
    }
    private Map<String, Integer> positionMap(List<String> values)
    {
        ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();

        int position = 0;
        for (String value : values) {
            builder.put(value, position++);
        }

        return builder.build();
    }
}
