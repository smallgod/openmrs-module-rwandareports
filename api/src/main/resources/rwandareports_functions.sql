#

DROP FUNCTION IF EXISTS person_family_name;
#

CREATE FUNCTION person_family_name(
    _person_id int
)
    RETURNS TEXT
    DETERMINISTIC

BEGIN
    DECLARE personFamilyName TEXT;

    select      family_name into personFamilyName
    from        person_name
    where       voided = 0
      and         person_id = _person_id
    order by    preferred desc, date_created desc
    limit       1;

    RETURN personFamilyName;

END
#

DROP FUNCTION IF EXISTS person_family_name_2;
#

CREATE FUNCTION person_family_name_2(
    _person_id int
)
    RETURNS TEXT
    DETERMINISTIC

BEGIN
    DECLARE personFamilyName TEXT;

    select      family_name2 into personFamilyName
    from        person_name
    where       voided = 0
      and         person_id = _person_id
    order by    preferred desc, date_created desc
    limit       1;

    RETURN personFamilyName;

END
#

DROP FUNCTION IF EXISTS person_given_name;
#

CREATE FUNCTION person_given_name(
    _person_id int
)
    RETURNS TEXT
    DETERMINISTIC

BEGIN
    DECLARE personGivenName TEXT;

    select      given_name into personGivenName
    from        person_name
    where       voided = 0
    and         person_id = _person_id
    order by    preferred desc, date_created desc
    limit       1;

    RETURN personGivenName;

END
#

DROP FUNCTION IF EXISTS health_center;
#

CREATE FUNCTION health_center(
    _patient_id int)

    RETURNS int
    DETERMINISTIC

BEGIN
    DECLARE  attVal int;

    select      cast(a.value as unsigned) into attVal
    from        person_attribute a
    where       person_attribute_type_id = 7
    and         a.voided = 0
    and         a.person_id = _patient_id
    order by    a.date_created desc
    limit       1;

    RETURN attVal;

END
#

#
DROP FUNCTION IF EXISTS obs_value_text;
#

CREATE FUNCTION obs_value_text(_encounterId int(11), _concept_id int(11))
    RETURNS text
    DETERMINISTIC

BEGIN

    DECLARE ret text;

    select      o.value_text into ret
    from        obs o
    where       o.voided = 0
    and         o.encounter_id = _encounterId
    and         o.concept_id = _concept_id
    order by    o.date_created desc, o.obs_id desc
    limit 1;

    RETURN ret;

END
#

#
DROP FUNCTION IF EXISTS encounter_with_text_obs_value;

#
CREATE FUNCTION encounter_with_text_obs_value(_concept_id int(11), _value_text varchar(255))
    RETURNS int
    DETERMINISTIC

BEGIN

    DECLARE ret int;

    select      o.encounter_id into ret
    from        obs o
    where       o.voided = 0
    and         o.concept_id = _concept_id
    and         o.value_text = _value_text
    order by    o.date_created desc, o.obs_id desc
    limit 1;

    RETURN ret;

END
#

DROP FUNCTION IF EXISTS concept_name;
#

CREATE FUNCTION concept_name(
    _conceptID INT
)
	RETURNS VARCHAR(255)
    DETERMINISTIC

BEGIN
    DECLARE conceptName varchar(255);

	SELECT name INTO conceptName
	FROM concept_name
	WHERE voided = 0
	  AND concept_id = _conceptID
	order by if(locale = 'en', 0, 1),
	  locale_preferred desc, ISNULL(concept_name_type) asc,
	  field(concept_name_type,'FULLY_SPECIFIED','SHORT')
	limit 1;

    RETURN conceptName;
END
#

DROP FUNCTION IF EXISTS obs_single_value_coded;
#

CREATE FUNCTION obs_single_value_coded(_encounterId int(11), _question_concept_id int)
    RETURNS varchar(255)
    DETERMINISTIC

BEGIN

    DECLARE ret varchar(255);

    select      concept_name(o.value_coded) into ret
    from        obs o
    where       o.voided = 0
      and         o.encounter_id = _encounterId
      and         o.concept_id = _question_concept_id
    order by    o.date_created desc, o.obs_id desc
    limit 1;

    RETURN ret;

END
#

DROP FUNCTION IF EXISTS obs_single_value_uuid;
#

CREATE FUNCTION obs_single_value_uuid(_encounterId int(11), _question_concept_id int)
    RETURNS varchar(255)
    DETERMINISTIC

BEGIN

    DECLARE ret varchar(255);

    select      o.uuid into ret
    from        obs o
    where       o.voided = 0
      and         o.encounter_id = _encounterId
      and         o.concept_id = _question_concept_id
    order by    o.date_created desc, o.obs_id desc
    limit 1;

    RETURN ret;

END
#
