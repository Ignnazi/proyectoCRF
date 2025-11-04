package ingsof.config;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;

/**
 * Custom ID generator that works with database triggers.
 * The actual ID generation is handled by the database trigger,
 * this generator just returns null to let the trigger do its job.
 */
public class TriggerAssignedIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        // Return null to let the trigger generate the ID
        return null;
    }
}
