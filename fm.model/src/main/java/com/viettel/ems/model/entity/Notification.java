package com.viettel.ems.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.utils.condition.Column;
import com.viettel.utils.condition.Table;
import lombok.*;
import org.onap.ves.DCAE;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@Table("event")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Notification extends Event {

    @JsonProperty("additional_info")
    @Column(name = "additional_info")
    private Map<String, String> additionalInfo;

    @JsonProperty("array_of_named_hash_map")
    @Column(name = "array_of_named_hash_map")
    private DCAE.Event.NamedHashMap[] arrayOfNamedHashMap;

    @JsonProperty("change_contact")
    @Column(name = "change_contact")
    private String changeContact;

    @JsonProperty("change_identifier")
    @Column(name = "change_identifier")
    private String changeIdentifier;

    @JsonProperty("change_type")
    @Column(name = "change_type")
    private String changeType;

    @JsonProperty("new_state")
    @Column(name = "new_state")
    private String newState;

    @JsonProperty("notification_fields_version")
    @Column(name = "notification_fields_version")
    private String fieldsVersion;

    @JsonProperty("old_state")
    @Column(name = "old_state")
    private String oldState;

    @JsonProperty("state_interface")
    @Column(name = "state_interface")
    private String stateInterface;

    public static Notification fromProto(DCAE.VesEvent event) {
        var notification = Event.fromProto(event, new Notification());
        var fields = event.getEvent().getNotificationFields();

        notification.setAdditionalInfo(fields.getAdditionalFieldsMap());
        notification.setArrayOfNamedHashMap(fields.getArrayOfNamedHashMapList()
            .toArray(new DCAE.Event.NamedHashMap[0]));
        notification.setFieldsVersion(fields.getNotificationFieldsVersion());
        notification.setChangeContact(fields.getChangeContact());
        notification.setChangeIdentifier(fields.getChangeIdentifier());
        notification.setChangeType(fields.getChangeType());
        notification.setOldState(fields.getOldState());
        notification.setNewState(fields.getNewState());
        notification.setStateInterface(fields.getStateInterface());

        return notification;
    }

    @Component
    public static class Mapper implements RowMapper<Notification> {
        @Override
        public Notification mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            var notification = Event.fromResultSet(rs, new Notification());

            notification.setFieldsVersion(rs.getString("notification_fields_version"));
            notification.setChangeContact(rs.getString("change_contact"));
            notification.setChangeIdentifier(rs.getString("change_identifier"));
            notification.setChangeType(rs.getString("change_type"));
            notification.setOldState(rs.getString("old_state"));
            notification.setNewState(rs.getString("new_state"));
            notification.setStateInterface(rs.getString("state_interface"));

            try {
                var additional = rs.getString("additional_info");
                var info = new ObjectMapper().readValue(additional, new TypeReference<HashMap<String, String>>() { });
                notification.setAdditionalInfo(info);
            } catch (JsonProcessingException ignored) {
            }

            return notification;
        }
    }
}
